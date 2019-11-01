module Page.DeviceListPage exposing (Model, Msg, init, update, view)

import Api.Device exposing (Device, devicesDecoder)
import Html exposing (..)
import Html.Attributes exposing (..)
import Html.Events exposing (onClick)
import Http
import Json.Decode as Decode
import RemoteData exposing (WebData)
import View.Menu exposing (viewMenu)


type alias Model =
    { devices : WebData (List Device) }


type Msg
    = FetchDeviceListPage
    | DeviceListPageReceived (WebData (List Device))


init : ( Model, Cmd Msg )
init =
    ( { devices = RemoteData.Loading }, fetchDeviceListPage )


fetchDeviceListPage : Cmd Msg
fetchDeviceListPage =
    Http.get
        { url = "http://localhost:8080/iotdevices/rest/devices"
        , expect =
            devicesDecoder
                |> Http.expectJson (RemoteData.fromResult >> DeviceListPageReceived)
        }


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        FetchDeviceListPage ->
            ( { model | devices = RemoteData.Loading }, fetchDeviceListPage )

        DeviceListPageReceived response ->
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
            div []
                [ h3 [] [ text "devices" ]
                , table []
                    ([ viewTableHeader ] ++ List.map viewPost actualdevices)
                ]

        RemoteData.Failure httpError ->
            viewFetchError (buildErrorMessage httpError)


viewTableHeader : Html Msg
viewTableHeader =
    tr []
        [ th []
            [ text "ID" ]
        , th []
            [ text "Title" ]
        , th []
            [ text "Author" ]
        ]


viewPost : Device -> Html Msg
viewPost device =
    tr []
        [ td []
            [ text (String.fromInt device.id) ]
        , td []
            [ text device.name ]
        , td []
            [ text device.status ]
        ]


viewFetchError : String -> Html Msg
viewFetchError errorMessage =
    let
        errorHeading =
            "Couldn't fetch devices at this time."
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
