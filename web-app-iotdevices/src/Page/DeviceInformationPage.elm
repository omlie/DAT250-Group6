module Page.DeviceInformationPage exposing (Model, Msg, fetchDevice, init, update, view)

import Api.Device exposing (Device, deviceDecoder)
import Api.DeviceSubscribers exposing (DeviceSubscribers)
import Api.Feedback exposing (Feedback, feedbackDecoder, feedbackListDecoder)
import Api.Subscription exposing (SubscriptionStatus, subscriptionStatusDecoder)
import Api.User exposing (User)
import Browser.Navigation exposing (load)
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
    , deleteError : Bool
    , subscriptionStatus : WebData SubscriptionStatus
    , approvedSubscribers : WebData DeviceSubscribers
    }


type Msg
    = DeviceReceived (WebData Device)
    | FeedbackChanged String
    | FeedbackSubmitted (WebData Feedback)
    | SubmitFeedback
    | FeedbackReceived (WebData (List Feedback))
    | Subscribe
    | Unsubscribe
    | Delete Device
    | DeviceDeleted (Result Http.Error ())
    | SubscriptionStatusReceived (WebData SubscriptionStatus)
    | SubscriptionAction (Result Http.Error ())


init : User -> Int -> ( Model, Cmd Msg )
init user deviceId =
    ( { user = user
      , device = RemoteData.Loading
      , deviceId = deviceId
      , feedback = RemoteData.NotAsked
      , newFeedback = ""
      , deleteError = False
      , response = RemoteData.NotAsked
      , subscriptionStatus = RemoteData.NotAsked
      , approvedSubscribers = RemoteData.NotAsked
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


delete : Model -> Device -> Cmd Msg
delete model device =
    Http.request
        { method = "POST"
        , body = Http.emptyBody
        , headers = [ Http.header "userId" (String.fromInt model.user.id), Http.header "deviceId" (String.fromInt device.id) ]
        , expect = Http.expectWhatever DeviceDeleted
        , url = "http://localhost:8080/iotdevices/rest/devices/delete"
        , timeout = Nothing
        , tracker = Nothing
        }


redirect : Cmd Msg
redirect =
    load "/mypage"


getSubscriptionStatus : Model -> Cmd Msg
getSubscriptionStatus model =
    Http.get
        { url = "http://localhost:8080/iotdevices/rest/subscription/status/" ++ String.fromInt model.deviceId ++ "/" ++ String.fromInt model.user.id
        , expect =
            subscriptionStatusDecoder
                |> Http.expectJson (RemoteData.fromResult >> SubscriptionStatusReceived)
        }


getApprovedSubscribers : Model -> Cmd Msg
getApprovedSubscribers model =
    Http.get
        { url = "http://localhost:8080/iotdevices/rest/devices/" ++ String.fromInt model.deviceId ++ "/approvedSubscribers"
        , expect =
            subscriptionStatusDecoder
                |> Http.expectJson (RemoteData.fromResult >> SubscriptionStatusReceived)
        }


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        DeviceReceived response ->
            ( { model | device = response, feedback = RemoteData.Loading }, Cmd.batch [ fetchFeedback model.deviceId, getApprovedSubscribers model ] )

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

        Delete device ->
            ( model, delete model device )

        DeviceDeleted (Result.Ok _) ->
            ( model, redirect )

        DeviceDeleted _ ->
            ( { model | deleteError = True }, Cmd.none )

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
                    button [ onClick Subscribe, class "submitbutton" ] [ text "Subscribe" ]

                "Approved" ->
                    button [ onClick Unsubscribe, class "submitbutton" ] [ text "Unsubscribe" ]

                "Owner" ->
                    case model.device of
                        RemoteData.Success device ->
                            div [ class "buttonrowVertical" ]
                                [ a [ href ("/device/edit/" ++ String.fromInt device.id), class "submitbutton" ]
                                    [ text "Edit device" ]
                                , button
                                    [ onClick (Delete device), class "submitbutton" ]
                                    [ text "Delete device" ]
                                , deleteFailed model.deleteError
                                ]

                        _ ->
                            text "Loading device..."

                _ ->
                    h3 [] [ text statusMessage.status ]

        RemoteData.Failure httpError ->
            viewFetchError (buildErrorMessage httpError) " your relation to the device "

        _ ->
            h3 [] [ text "Loading..." ]


deleteFailed : Bool -> Html Msg
deleteFailed failed =
    if failed then
        div [] [ text "Deletion failed" ]

    else
        text ""


giveFeedback : Model -> Html Msg
giveFeedback model =
    case model.approvedSubscribers of
        RemoteData.Success subscribers ->
            let
                userHasApprovedSubscription =
                    List.any (\subscriber -> subscriber == model.user) subscribers
            in
            if userHasApprovedSubscription then
                div
                    [ class "feedbackForm" ]
                    [ h3 [] [ text "Provide feedback" ]
                    , input [ onInput FeedbackChanged, value model.newFeedback ] []
                    , submitButton model
                    ]

            else
                text ""

        _ ->
            text ""


submitButton : Model -> Html Msg
submitButton model =
    case model.newFeedback of
        "" ->
            text ""

        _ ->
            button [ class "submitButton", onClick SubmitFeedback ] [ text "Submit" ]


view : Model -> Html Msg
view model =
    viewDevice model.device model.feedback (giveFeedback model) (subscribeButton model)


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
