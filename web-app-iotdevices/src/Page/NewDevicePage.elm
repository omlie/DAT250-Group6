module Page.NewDevicePage exposing (Model, Msg, init, update, view)

import Api.Device exposing (Device, deviceDecoder)
import Browser.Navigation exposing (load)
import Html exposing (Html, button, div, input, option, select, text)
import Html.Attributes exposing (class, placeholder, value)
import Html.Events exposing (onClick, onInput)
import Http
import Json.Encode exposing (Value, int, list, object, string)
import RemoteData exposing (WebData)


type alias Model =
    { devicename : String
    , deviceimg : String
    , apiurl : String
    , status : Int
    , labels : List String
    , ownerId : Int
    }


type Msg
    = DeviceNameChange String
    | DeviceImageChange String
    | ApiUrlChange String
    | StatusChange String
    | AddDevice
    | DeviceAdded (WebData Device)


init : ( Model, Cmd Msg )
init =
    ( { devicename = ""
      , deviceimg = ""
      , apiurl = ""
      , status = 0
      , labels = []
      , ownerId = 1
      }
    , Cmd.none
    )


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        DeviceNameChange name ->
            ( { model | devicename = name }, Cmd.none )

        DeviceImageChange url ->
            ( { model | deviceimg = url }, Cmd.none )

        ApiUrlChange url ->
            ( { model | apiurl = url }, Cmd.none )

        StatusChange status ->
            ( { model | status = Maybe.withDefault 0 (String.toInt status) }, Cmd.none )

        AddDevice ->
            ( model, addDevice model )

        DeviceAdded response ->
            ( model, redirect response )


redirect : WebData Device -> Cmd Msg
redirect device =
    case device of
        RemoteData.Success actualdevice ->
            load ("http://localhost:8000/device/" ++ String.fromInt actualdevice.id)

        _ ->
            load "http://localhost:8000/mypage"


addDevice : Model -> Cmd Msg
addDevice model =
    Http.request
        { body = Http.jsonBody (encodeDevice model)
        , method = "POST"
        , headers = []
        , expect =
            deviceDecoder
                |> Http.expectJson (RemoteData.fromResult >> DeviceAdded)
        , url = "http://localhost:8080/iotdevices/rest/devices/create"
        , timeout = Nothing
        , tracker = Nothing
        }


encodeDevice : Model -> Value
encodeDevice model =
    let
        bodylist =
            [ ( "devicename", string model.devicename )
            , ( "apiurl", string model.apiurl )
            , ( "deviceimg", string model.deviceimg )
            , ( "status", int model.status )
            , ( "labels", list string model.labels )
            , ( "ownerId", int model.ownerId )
            ]
    in
    bodylist
        |> object



-- VIEWS


view : Model -> Html Msg
view model =
    viewForm model


viewForm : Model -> Html Msg
viewForm model =
    div [ class "form" ]
        [ input [ placeholder "Device name", model.devicename |> value, onInput DeviceNameChange ] []
        , input [ placeholder "Device image URL", model.deviceimg |> value, onInput DeviceImageChange ] []
        , input [ placeholder "API URL", model.apiurl |> value, onInput ApiUrlChange ] []
        , statusRadioButtons
        , button [ class "submitbutton", onClick AddDevice ] [ text "Add device" ]
        ]


statusRadioButtons : Html Msg
statusRadioButtons =
    select [ onInput StatusChange ]
        [ option [ value "0" ] [ text "Online" ]
        , option [ value "1" ] [ text "Offline" ]
        ]
