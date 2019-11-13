module Page.DeviceInformationPage exposing (Model, Msg, fetchDevice, init, update, view)

import Api.Device exposing (Device, deviceDecoder)
import Api.Feedback exposing (Feedback, feedbackDecoder, feedbackListDecoder)
import Api.Subscription exposing (SubscriptionStatus, subscriptionStatusDecoder)
import Api.User exposing (User)
import Html exposing (Html, a, button, div, h3, input, text)
import Html.Attributes exposing (class, href, value)
import Html.Events exposing (onClick, onInput)
import Http
import Json.Encode exposing (Value, int, object, string)
import RemoteData exposing (WebData)
import View.DeviceViews exposing (deviceInformation)
import View.ErrorViews exposing (buildErrorMessage, viewFetchError)


type alias Model =
    { device : WebData Device
    , feedback : WebData (List Feedback)
    , deviceId : Int
    , newFeedback : String
    , response : WebData Feedback
    , user : User
    , subscriptionStatus : WebData SubscriptionStatus
    }


type Msg
    = DeviceReceived (WebData Device)
    | FeedbackChanged String
    | FeedbackSubmitted (WebData Feedback)
    | SubmitFeedback
    | FeedbackReceived (WebData (List Feedback))
    | Subscribe
    | Unsubscribe
    | SubscriptionStatusReceived (WebData SubscriptionStatus)
    | SubscriptionAction (Result Http.Error ())


init : User -> Int -> ( Model, Cmd Msg )
init user deviceId =
    ( { user = user
      , device = RemoteData.Loading
      , deviceId = deviceId
      , feedback = RemoteData.NotAsked
      , newFeedback = ""
      , response = RemoteData.NotAsked
      , subscriptionStatus = RemoteData.NotAsked
      }
    , fetchDevice deviceId
    )


fetchDevice : Int -> Cmd Msg
fetchDevice deviceId =
    Http.get
        { url = "http://localhost:8080/iotdevices/rest/devices/" ++ String.fromInt deviceId
        , expect =
            deviceDecoder
                |> Http.expectJson (RemoteData.fromResult >> DeviceReceived)
        }


fetchFeedback : Int -> Cmd Msg
fetchFeedback deviceId =
    Http.get
        { url = "http://localhost:8080/iotdevices/rest/devices/" ++ String.fromInt deviceId ++ "/feedback"
        , expect =
            feedbackListDecoder
                |> Http.expectJson (RemoteData.fromResult >> FeedbackReceived)
        }


submitFeedback : Model -> Cmd Msg
submitFeedback model =
    Http.request
        { method = "POST"
        , body = Http.jsonBody (encodeFeedback model)
        , headers = []
        , expect =
            feedbackDecoder
                |> Http.expectJson (RemoteData.fromResult >> FeedbackSubmitted)
        , url = "http://localhost:8080/iotdevices/rest/devices/feedback"
        , timeout = Nothing
        , tracker = Nothing
        }


subscribe : Model -> Cmd Msg
subscribe model =
    Http.request
        { method = "POST"
        , body = Http.emptyBody
        , headers = []
        , expect = Http.expectWhatever SubscriptionAction
        , url = "http://localhost:8080/iotdevices/rest/subscription/" ++ String.fromInt model.deviceId ++ "/" ++ String.fromInt model.user.id
        , timeout = Nothing
        , tracker = Nothing
        }


unsubscribe : Model -> Cmd Msg
unsubscribe model =
    Http.request
        { method = "POST"
        , body = Http.emptyBody
        , headers = []
        , expect = Http.expectWhatever SubscriptionAction
        , url = "http://localhost:8080/iotdevices/rest/subscription/unsubscribe/" ++ String.fromInt model.deviceId ++ "/" ++ String.fromInt model.user.id
        , timeout = Nothing
        , tracker = Nothing
        }


getSubscriptionStatus : Model -> Cmd Msg
getSubscriptionStatus model =
    Http.get
        { url = "http://localhost:8080/iotdevices/rest/subscription/status/" ++ String.fromInt model.deviceId ++ "/" ++ String.fromInt model.user.id
        , expect =
            subscriptionStatusDecoder
                |> Http.expectJson (RemoteData.fromResult >> SubscriptionStatusReceived)
        }


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        DeviceReceived response ->
            ( { model | device = response, feedback = RemoteData.Loading }, fetchFeedback model.deviceId )

        FeedbackReceived response ->
            ( { model | feedback = response, subscriptionStatus = RemoteData.Loading }, getSubscriptionStatus model )

        SubscriptionStatusReceived response ->
            ( { model | subscriptionStatus = response }, Cmd.none )

        FeedbackSubmitted _ ->
            ( model, fetchFeedback model.deviceId )

        SubmitFeedback ->
            ( { model | newFeedback = "" }, submitFeedback model )

        FeedbackChanged changedFeedback ->
            ( { model | newFeedback = changedFeedback }, Cmd.none )

        Subscribe ->
            ( model, subscribe model )

        Unsubscribe ->
            ( model, unsubscribe model )

        SubscriptionAction _ ->
            ( model, getSubscriptionStatus model )


encodeFeedback : Model -> Value
encodeFeedback model =
    let
        feedbacklist =
            [ ( "deviceid", int model.deviceId ), ( "userid", int model.user.id ), ( "feedback", string model.newFeedback ) ]
    in
    feedbacklist
        |> object



-- VIEWS


subscribeButton : Model -> Html Msg
subscribeButton model =
    case model.subscriptionStatus of
        RemoteData.Success statusMessage ->
            case statusMessage.status of
                "none" ->
                    button [ onClick Subscribe, class "submitButton" ] [ text "Subscribe" ]

                "Approved" ->
                    button [ onClick Unsubscribe, class "submitButton" ] [ text "Unsubscribe" ]

                "Owner" ->
                    case model.device of
                        RemoteData.Success device ->
                            a [ href ("/device/edit/" ++ String.fromInt device.id), class "submitButton" ]
                                [ div [] [ text "Edit device" ] ]

                        _ ->
                            text "Loading device..."

                _ ->
                    h3 [] [ text statusMessage.status ]

        RemoteData.Failure httpError ->
            viewFetchError (buildErrorMessage httpError) " your relation to the device "

        _ ->
            h3 [] [ text "Loading..." ]


giveFeedback : Model -> Html Msg
giveFeedback model =
    div [ class "feedbackForm" ]
        [ h3 [] [ text "Provide feedback" ]
        , input [ onInput FeedbackChanged, value model.newFeedback ] []
        , submitButton model
        ]


submitButton : Model -> Html Msg
submitButton model =
    case model.newFeedback of
        "" ->
            text ""

        _ ->
            button [ class "submitButton", onClick SubmitFeedback ] [ text "Submit" ]


view : Model -> Html Msg
view model =
    div []
        [ viewDevice model.device model.feedback (giveFeedback model) (subscribeButton model)
        ]


viewDevice : WebData Device -> WebData (List Feedback) -> Html Msg -> Html Msg -> Html Msg
viewDevice device feedback feedbackform subBtn =
    case ( device, feedback ) of
        ( RemoteData.NotAsked, _ ) ->
            text ""

        ( RemoteData.Loading, _ ) ->
            h3 [] [ text "Loading..." ]

        ( RemoteData.Success actualDevice, RemoteData.Success actualFeedback ) ->
            deviceInformation actualDevice actualFeedback feedbackform subBtn

        ( RemoteData.Failure httpError, _ ) ->
            viewFetchError (buildErrorMessage httpError) " the device "

        ( _, RemoteData.Failure httpError ) ->
            viewFetchError (buildErrorMessage httpError) " feedback "

        ( _, _ ) ->
            h3 [] [ text "Loading..." ]
