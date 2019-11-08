module Page.DeviceInformationPage exposing (Model, Msg, init, submitFeedback, update, view)

import Api.Device exposing (Device, deviceDecoder)
import Api.Feedback exposing (Feedback, feedbackDecoder, feedbackListDecoder)
import Html exposing (Html, button, div, input, text, h2, textarea, h3)
import Html.Attributes exposing (class, placeholder, value)
import Html.Events exposing (onClick, onInput)
import Json.Encode exposing (Value, int, list, object, string)
import Http
import RemoteData exposing (WebData)
import View.DeviceViews exposing (deviceInformation)
import View.ErrorViews exposing (buildErrorMessage, viewFetchError)


type alias Model =
    { device : WebData Device
    , feedback : WebData (List Feedback)
    , deviceId : Int
    , newFeedback : String
    , response : WebData Feedback
    }


type Msg
    = DeviceReceived (WebData Device)
    | FeedbackReceived (WebData (List Feedback))
    | FeedbackSubmitted (WebData Feedback)
    | SubmitFeedback
    | FeedbackChanged String


init : Int -> ( Model, Cmd Msg )
init deviceId =
    ( { device = RemoteData.Loading, deviceId = deviceId, feedback = RemoteData.NotAsked, newFeedback = "", response = RemoteData.NotAsked }, fetchDevice deviceId )


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
        , body = Http.jsonBody (encodeFeedback  model)
        , headers = []
        , expect =
            feedbackDecoder
                |> Http.expectJson (RemoteData.fromResult >> FeedbackSubmitted)
        , url = "http://localhost:8080/iotdevices/rest/devices/give/feedback/" ++ String.fromInt model.deviceId ++ "/1"
        , timeout = Nothing
        , tracker = Nothing
        }


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        DeviceReceived response ->
            ( { model | device = response, feedback = RemoteData.Loading }, fetchFeedback model.deviceId )

        FeedbackReceived response ->
            ( { model | feedback = response }, Cmd.none )

        FeedbackSubmitted response ->
            ( { model | response = response }, Cmd.none )

        SubmitFeedback ->
            ( { model | newFeedback = "" }, submitFeedback model )

        FeedbackChanged changedFeedback ->
            ( { model | newFeedback = changedFeedback }, Cmd.none )


encodeFeedback : Model -> Value
encodeFeedback model =
    let feedbacklist = [ ( "deviceid", int model.deviceId ), ( "userid", int 1), ( "feedback", string model.newFeedback )]
    in
    feedbacklist
        |> object



-- VIEWS


giveFeedback : Model -> Html Msg
giveFeedback model =
    div [ class "giveFeedback" ]
    [
        div [ class "feedbackElements" ]
            [ h2 [] [ text "Provide feedback" ]
            , textarea [ onInput FeedbackChanged, value model.newFeedback ] []
            , button [ onClick SubmitFeedback ] [ text "Submit" ]
            ]
    ]


view : Model -> Html Msg
view model =
    div []
        [ viewDevice model.device model.feedback
        , giveFeedback model
        ]


viewDevice : WebData Device -> WebData (List Feedback) -> Html Msg
viewDevice device feedback =
    case ( device, feedback ) of
        ( RemoteData.NotAsked, _ ) ->
            text ""

        ( RemoteData.Loading, _ ) ->
            h3 [] [ text "Loading..." ]

        ( RemoteData.Success actualDevice, RemoteData.Success actualFeedback ) ->
            deviceInformation actualDevice actualFeedback

        ( RemoteData.Failure httpError, _ ) ->
            viewFetchError (buildErrorMessage httpError)

        ( _, RemoteData.Failure httpError ) ->
            viewFetchError (buildErrorMessage httpError)

        ( _, _ ) ->
            h3 [] [ text "Loading..." ]
