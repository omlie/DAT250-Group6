module Page.UserInformationPage exposing (Model, Msg, init, update, view)

import Api.Device exposing (Device, devicesDecoder)
import Api.User exposing (User, userDecoder)
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
    { user : WebData User
    , ownedDevices : WebData (List Device)
    , subscribedDevices : WebData (List Device)
    }


type Msg
    = FetchUser
    | FetchOwnedDevices
    | FetchSubscribedDevices
    | UserReceived (WebData User)
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
        { url = "http://localhost:8080/iotdevices/rest/users/1/subscribedDevices"
        , expect =
            devicesDecoder
                |> Http.expectJson (RemoteData.fromResult >> SubscribedDevicesReceived)
        }


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        FetchUser ->
            ( { model | user = RemoteData.Loading }, fetchUser )

        FetchOwnedDevices ->
            ( { model | ownedDevices = RemoteData.Loading }, fetchOwnedDevices )

        FetchSubscribedDevices ->
            ( { model | subscribedDevices = RemoteData.Loading }, fetchSubscribedDevices )

        UserReceived response ->
            ( { model | user = response }, fetchOwnedDevices )

        OwnedDevicesReceived response ->
            ( { model | ownedDevices = response }, fetchSubscribedDevices )

        SubscribedDevicesReceived response ->
            ( { model | subscribedDevices = response }, Cmd.none )



-- VIEWS


view : Model -> Html Msg
view model =
    div [ class "wrapper" ]
        [ viewMenu
        , div [ class "content" ]
            [ viewUser model.user
            , viewDevices "Owned devices" model.ownedDevices
            , viewDevices "Subscribed devices" model.subscribedDevices
            ]
        ]


viewUser : WebData User -> Html Msg
viewUser user =
    case user of
        RemoteData.NotAsked ->
            text ""

        RemoteData.Loading ->
            h3 [] [ text "Loading..." ]

        RemoteData.Success actualUser ->
            div []
                [ text ("Hello " ++ actualUser.username) ]

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
