module Page.DeviceListPage exposing (Model, Msg, init, update, view)

import Api.Device exposing (Device, devicesDecoder)
import Html exposing (..)
import Html.Attributes exposing (..)
import Html.Events exposing (onClick, onInput)
import Http
import Json.Decode as Decode
import RemoteData exposing (WebData)
import View.DeviceViews exposing (deviceList)
import View.ErrorViews exposing (..)
import View.Menu exposing (viewMenu)


type alias Model =
    { devices : WebData (List Device), searchBarContent : String }


type Msg
    = FetchDevices
    | DevicesReceived (WebData (List Device))
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
        FetchDevices ->
            ( { model | devices = RemoteData.Loading }, fetchDevices )

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
    let
        lengthOfFilter =
            String.length filterOn
    in
    String.left lengthOfFilter (String.toLower device.deviceName) == String.toLower filterOn
