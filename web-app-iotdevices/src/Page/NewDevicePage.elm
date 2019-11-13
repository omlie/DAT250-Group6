module Page.NewDevicePage exposing (Model, Msg, encodeDevice, init, update, view)

import Api.Device exposing (Device, deviceDecoder)
import Api.User exposing (User)
import Browser.Navigation exposing (Key, pushUrl)
import Html exposing (Html, button, div, input, option, select, text)
import Html.Attributes exposing (class, placeholder, value)
import Html.Events exposing (onClick, onInput)
import Http
import Json.Encode exposing (Value, int, list, object, string)
import RemoteData exposing (WebData)
import String exposing (toInt)


type alias Model =
    { deviceId : Int
    , deviceName : String
    , deviceImg : String
    , apiUrl : String
    , status : Int
    , labels : List String
    , ownerId : Int
    , navKey : Key
    , isEditing : Bool
    }


type Msg
    = DeviceNameChange String
    | DeviceImageChange String
    | ApiUrlChange String
    | StatusChange String
    | AddDevice
    | DeviceAdded (WebData Device)
    | DeviceReceived (WebData Device)
    | EditDevice
    | DeviceEdited (WebData Device)


init : Key -> User -> Maybe Int -> ( Model, Cmd Msg )
init key user maybeDeviceId =
    let
        command =
            case maybeDeviceId of
                Just deviceId ->
                    fetchDevice deviceId

                Nothing ->
                    Cmd.none
    in
    ( { deviceId = 0
      , deviceName = ""
      , deviceImg = ""
      , apiUrl = ""
      , status = 0
      , labels = []
      , ownerId = user.id
      , navKey = key
      , isEditing = False
      }
    , command
    )


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        DeviceNameChange name ->
            ( { model | deviceName = name }, Cmd.none )

        DeviceImageChange url ->
            ( { model | deviceImg = url }, Cmd.none )

        ApiUrlChange url ->
            ( { model | apiUrl = url }, Cmd.none )

        StatusChange status ->
            ( { model | status = Maybe.withDefault 0 (String.toInt status) }, Cmd.none )

        AddDevice ->
            ( model, addDevice model )

        DeviceAdded response ->
            ( model, redirect model.navKey response )

        DeviceReceived response ->
            case response of
                RemoteData.Success actualDevice ->
                    let
                        deviceStatus =
                            case toInt actualDevice.status of
                                Just status ->
                                    status

                                Nothing ->
                                    0
                    in
                    ( { model | deviceId = actualDevice.id, deviceName = actualDevice.deviceName, deviceImg = actualDevice.deviceImg, apiUrl = actualDevice.apiUrl, status = deviceStatus, ownerId = actualDevice.owner.id, isEditing = True }, Cmd.none )

                _ ->
                    ( model, Cmd.none )

        EditDevice ->
            ( model, editDevice model )

        DeviceEdited response ->
            ( model, redirect model.navKey response )


redirect : Key -> WebData Device -> Cmd Msg
redirect key device =
    case device of
        RemoteData.Success actualDevice ->
            pushUrl key ("http://localhost:8000/device/" ++ String.fromInt actualDevice.id)

        _ ->
            pushUrl key "http://localhost:8000/mypage"


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


editDevice : Model -> Cmd Msg
editDevice model =
    Http.request
        { body = Http.jsonBody (encodeDevice model)
        , method = "POST"
        , headers = []
        , expect =
            deviceDecoder
                |> Http.expectJson (RemoteData.fromResult >> DeviceEdited)
        , url = "http://localhost:8080/iotdevices/rest/devices/edit/" ++ String.fromInt model.deviceId
        , timeout = Nothing
        , tracker = Nothing
        }


encodeDevice : Model -> Value
encodeDevice model =
    let
        bodylist =
            [ ( "deviceName", string model.deviceName )
            , ( "apiUrl", string model.apiUrl )
            , ( "deviceImg", string model.deviceImg )
            , ( "status", int model.status )
            , ( "labels", list string model.labels )
            , ( "ownerId", int model.ownerId )
            ]
    in
    bodylist
        |> object


view : Model -> Html Msg
view model =
    viewForm model


viewForm : Model -> Html Msg
viewForm model =
    div [ class "form" ]
        [ input [ placeholder "Device name", model.deviceName |> value, onInput DeviceNameChange ] []
        , input [ placeholder "Device image URL", model.deviceImg |> value, onInput DeviceImageChange ] []
        , input [ placeholder "API URL", model.apiUrl |> value, onInput ApiUrlChange ] []
        , statusRadioButtons
        , let
            buttonText =
                if model.isEditing then
                    "Edit device"

                else
                    "Add device"

            action =
                if model.isEditing then
                    EditDevice

                else
                    AddDevice
          in
          button [ class "submitbutton", onClick action ] [ text buttonText ]
        ]


statusRadioButtons : Html Msg
statusRadioButtons =
    select [ onInput StatusChange ]
        [ option [ value "0" ] [ text "Online" ]
        , option [ value "1" ] [ text "Offline" ]
        ]


fetchDevice : Int -> Cmd Msg
fetchDevice deviceId =
    Http.get
        { url = "http://localhost:8080/iotdevices/rest/devices/" ++ String.fromInt deviceId
        , expect =
            deviceDecoder
                |> Http.expectJson (RemoteData.fromResult >> DeviceReceived)
        }
