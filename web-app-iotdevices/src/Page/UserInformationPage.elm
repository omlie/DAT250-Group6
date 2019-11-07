module Page.UserInformationPage exposing (Model, Msg, init, update, view)

import Api.Device exposing (Device, devicesDecoder)
import Api.User exposing (User, userDecoder)
import Html exposing (Html, div, h3, text)
import Http
import RemoteData exposing (WebData)
import View.DeviceViews exposing (deviceList)
import View.ErrorViews exposing (buildErrorMessage, viewFetchError)
import View.UserInfoViews exposing (viewUserInformation)


type alias Model =
    { user : WebData User
    , ownedDevices : WebData (List Device)
    , subscribedDevices : WebData (List Device)
    }


type Msg
    = UserReceived (WebData User)
    | OwnedDevicesReceived (WebData (List Device))
    | SubscribedDevicesReceived (WebData (List Device))


init : ( Model, Cmd Msg )
init =
    ( { user = RemoteData.Loading, ownedDevices = RemoteData.Loading, subscribedDevices = RemoteData.Loading }, fetchUser )


fetchUser : Cmd Msg
fetchUser =
    Http.get
        { url = "http://localhost:8080/iotdevices/rest/users/1"
        , expect =
            userDecoder
                |> Http.expectJson (RemoteData.fromResult >> UserReceived)
        }


fetchOwnedDevices : Cmd Msg
fetchOwnedDevices =
    Http.get
        { url = "http://localhost:8080/iotdevices/rest/users/1/devices"
        , expect =
            devicesDecoder
                |> Http.expectJson (RemoteData.fromResult >> OwnedDevicesReceived)
        }


fetchSubscribedDevices : Cmd Msg
fetchSubscribedDevices =
    Http.get
        { url = "http://localhost:8080/iotdevices/rest/subscription/1/subscribedDevices"
        , expect =
            devicesDecoder
                |> Http.expectJson (RemoteData.fromResult >> SubscribedDevicesReceived)
        }


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        UserReceived response ->
            ( { model | user = response }, fetchOwnedDevices )

        OwnedDevicesReceived response ->
            ( { model | ownedDevices = response }, fetchSubscribedDevices )

        SubscribedDevicesReceived response ->
            ( { model | subscribedDevices = response }, Cmd.none )



-- VIEWS


view : Model -> Html Msg
view model =
    div []
        [ viewUser model.user
        , viewDevices "Owned devices" model.ownedDevices
        , viewDevices "Subscribed devices" model.subscribedDevices
        ]


viewUser : WebData User -> Html Msg
viewUser user =
    case user of
        RemoteData.NotAsked ->
            text ""

        RemoteData.Loading ->
            h3 [] [ text "Loading..." ]

        RemoteData.Success actualUser ->
            viewUserInformation actualUser

        RemoteData.Failure httpError ->
            viewFetchError (buildErrorMessage httpError)


viewDevices : String -> WebData (List Device) -> Html Msg
viewDevices heading devices =
    case devices of
        RemoteData.NotAsked ->
            text ""

        RemoteData.Loading ->
            h3 [] [ text "Loading..." ]

        RemoteData.Success actualdevices ->
            deviceList heading actualdevices

        RemoteData.Failure httpError ->
            viewFetchError (buildErrorMessage httpError)
