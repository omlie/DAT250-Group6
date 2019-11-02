module Page.DeviceListPage exposing (Model, Msg, init, update, view)

import Api.Device exposing (Device, devicesDecoder)
import Html exposing (..)
import Html.Attributes exposing (..)
import Html.Events exposing (onClick)
import Http
import Json.Decode as Decode
import RemoteData exposing (WebData)
import View.DeviceViews exposing (deviceList)
import View.ErrorViews exposing (..)
import View.Menu exposing (viewMenu)


type alias Model =
    { devices : WebData (List Device) }


type Msg
    = FetchDevices
    | DevicesReceived (WebData (List Device))


init : ( Model, Cmd Msg )
init =
    ( { devices = RemoteData.Loading }, fetchDevices )


fetchDevices : Cmd Msg
fetchDevices =
    Http.get
        { url = "http://localhost:8080/iotdevices/rest/devices"
        , expect =
            devicesDecoder
                |> Http.expectJson (RemoteData.fromResult >> DevicesReceived)
        }


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        FetchDevices ->
            ( { model | devices = RemoteData.Loading }, fetchDevices )

        DevicesReceived response ->
            ( { model | devices = response }, Cmd.none )


view : Model -> Html Msg
view model =
    div [ class "wrapper" ]
        [ viewMenu
        , div [ class "content" ]
            [ viewDeviceListPage model.devices
            ]
        ]


viewDeviceListPage : WebData (List Device) -> Html Msg
viewDeviceListPage devices =
    case devices of
        RemoteData.NotAsked ->
            text ""

        RemoteData.Loading ->
            h3 [] [ text "Loading..." ]

        RemoteData.Success actualdevices ->
            deviceList "All devices" actualdevices

        RemoteData.Failure httpError ->
            viewFetchError (buildErrorMessage httpError)
