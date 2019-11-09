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
    { user : User
    , ownedDevices : WebData (List Device)
    , subscribedDevices : WebData (List Device)
    }


type Msg
    = OwnedDevicesReceived (WebData (List Device))
    | SubscribedDevicesReceived (WebData (List Device))


init : User -> ( Model, Cmd Msg )
init user =
    ( { user = user, ownedDevices = RemoteData.Loading, subscribedDevices = RemoteData.Loading }, Cmd.batch [ fetchOwnedDevices user, fetchSubscribedDevices user ] )


fetchOwnedDevices : User -> Cmd Msg
fetchOwnedDevices user =
    Http.get
        { url = "http://localhost:8080/iotdevices/rest/users/" ++ String.fromInt user.id ++ "/devices"
        , expect =
            devicesDecoder
                |> Http.expectJson (RemoteData.fromResult >> OwnedDevicesReceived)
        }


fetchSubscribedDevices : User -> Cmd Msg
fetchSubscribedDevices user =
    Http.get
        { url = "http://localhost:8080/iotdevices/rest/subscription/" ++ String.fromInt user.id ++ "/subscribedDevices"
        , expect =
            devicesDecoder
                |> Http.expectJson (RemoteData.fromResult >> SubscribedDevicesReceived)
        }


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        OwnedDevicesReceived response ->
            ( { model | ownedDevices = response }, Cmd.none )

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


viewUser : User -> Html Msg
viewUser user =
    viewUserInformation user


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
