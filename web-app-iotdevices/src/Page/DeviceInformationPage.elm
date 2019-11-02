module Page.DeviceInformationPage exposing (Model, Msg, init, update, view)

import Api.Device exposing (Device, deviceDecoder)
import Api.Feedback exposing (Feedback, feedbackListDecoder)
import Html exposing (..)
import Html.Attributes exposing (..)
import Html.Events exposing (onClick)
import Http
import Json.Decode as Decode
import RemoteData exposing (WebData)
import View.DeviceViews exposing (deviceInformation)
import View.ErrorViews exposing (..)
import View.Menu exposing (viewMenu)


type alias Model =
    { device : WebData Device
    , feedback : WebData (List Feedback)
    , deviceId : Int
    }


type Msg
    = FetchDevice
    | FetchFeedback
    | DeviceReceived (WebData Device)
    | FeedbackReceived (WebData (List Feedback))


init : Int -> ( Model, Cmd Msg )
init deviceId =
    ( { device = RemoteData.Loading, deviceId = deviceId, feedback = RemoteData.NotAsked }, fetchDevice deviceId )


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


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        FetchDevice ->
            ( { model | device = RemoteData.Loading }, fetchDevice model.deviceId )

        FetchFeedback ->
            ( { model | feedback = RemoteData.Loading }, fetchFeedback model.deviceId )

        DeviceReceived response ->
            ( { model | device = response, feedback = RemoteData.Loading }, fetchFeedback model.deviceId )

        FeedbackReceived response ->
            ( { model | feedback = response }, Cmd.none )



-- VIEWS


view : Model -> Html Msg
view model =
    viewDevice model.device model.feedback


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
