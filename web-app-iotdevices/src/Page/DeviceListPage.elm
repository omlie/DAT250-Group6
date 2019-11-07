module Page.DeviceListPage exposing (Model, Msg, init, update, view)

import Api.Device exposing (Device, devicesDecoder)
import Html exposing (Html, div, h3, input, text)
import Html.Attributes exposing (class, placeholder, value)
import Html.Events exposing (onInput)
import Http
import RemoteData exposing (WebData)
import View.DeviceViews exposing (deviceList)
import View.ErrorViews exposing (buildErrorMessage, viewFetchError)


type alias Model =
    { devices : WebData (List Device)
    , searchBarContent : String
    }


type Msg
    = DevicesReceived (WebData (List Device))
    | SearchDevice String


init : ( Model, Cmd Msg )
init =
    ( { devices = RemoteData.Loading, searchBarContent = "" }, fetchDevices )


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
        DevicesReceived response ->
            ( { model | devices = response }, Cmd.none )

        SearchDevice searchText ->
            ( { model | searchBarContent = searchText }, Cmd.none )


view : Model -> Html Msg
view model =
    div [ class "devicePage" ]
        [ input [ placeholder "Search for a device", value model.searchBarContent, onInput SearchDevice ] []
        , viewDeviceListPage model.devices model.searchBarContent
        ]


viewDeviceListPage : WebData (List Device) -> String -> Html Msg
viewDeviceListPage devices searchBarContent =
    case devices of
        RemoteData.NotAsked ->
            text ""

        RemoteData.Loading ->
            h3 [] [ text "Loading..." ]

        RemoteData.Success actualdevices ->
            deviceList "All devices" (filterDevices actualdevices searchBarContent)

        RemoteData.Failure httpError ->
            viewFetchError (buildErrorMessage httpError)


filterDevices : List Device -> String -> List Device
filterDevices devices filterOn =
    List.filter (\device -> deviceNameFitsSearchBar device filterOn) devices


deviceNameFitsSearchBar : Device -> String -> Bool
deviceNameFitsSearchBar device filterOn =
    String.contains
        (String.toLower filterOn)
        (String.toLower device.deviceName)
