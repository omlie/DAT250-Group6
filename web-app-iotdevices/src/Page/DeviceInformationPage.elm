module Page.DeviceInformationPage exposing (Model, Msg, init, update, view)

import Api.Device exposing (Device, deviceDecoder)
import Html exposing (..)
import Html.Attributes exposing (..)
import Html.Events exposing (onClick)
import Http
import Json.Decode as Decode
import RemoteData exposing (WebData)
import View.Menu exposing (viewMenu)


type alias Model =
    { device : WebData Device
    , deviceId : Int
    }


type Msg
    = FetchDevice
    | DeviceReceived (WebData Device)


init : Int -> ( Model, Cmd Msg )
init deviceId =
    ( { device = RemoteData.Loading, deviceId = deviceId }, fetchDevice deviceId )


fetchDevice : Int -> Cmd Msg
fetchDevice deviceId =
    Http.get
        { url = "http://localhost:8080/iotdevices/rest/devices/" ++ String.fromInt deviceId
        , expect =
            deviceDecoder
                |> Http.expectJson (RemoteData.fromResult >> DeviceReceived)
        }


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        FetchDevice ->
            ( { model | device = RemoteData.Loading }, fetchDevice model.deviceId )

        DeviceReceived response ->
            ( { model | device = response }, Cmd.none )



-- VIEWS


view : Model -> Html Msg
view model =
    div [ class "wrapper" ]
        [ viewMenu
        , div [ class "content" ] [ viewDevice model.device ]
        ]


viewDevice : WebData Device -> Html Msg
viewDevice device =
    case device of
        RemoteData.NotAsked ->
            text ""

        RemoteData.Loading ->
            h3 [] [ text "Loading..." ]

        RemoteData.Success actualDevice ->
            div []
                [ text ("Hello " ++ actualDevice.deviceName) ]

        RemoteData.Failure httpError ->
            viewFetchError (buildErrorMessage httpError)


viewFetchError : String -> Html Msg
viewFetchError errorMessage =
    let
        errorHeading =
            "Couldn't fetch Device at this time."
    in
    div []
        [ h3 [] [ text errorHeading ]
        , text ("Error: " ++ errorMessage)
        ]


buildErrorMessage : Http.Error -> String
buildErrorMessage httpError =
    case httpError of
        Http.BadUrl message ->
            message

        Http.Timeout ->
            "Server is taking too long to respond. Please try again later."

        Http.NetworkError ->
            "Unable to reach server."

        Http.BadStatus statusCode ->
            "Request failed with status code: " ++ String.fromInt statusCode

        Http.BadBody message ->
            message
