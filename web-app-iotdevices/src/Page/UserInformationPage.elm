module Page.UserInformationPage exposing (Model, Msg, init, update, view)

import Api.Device exposing (Device, devicesDecoder)
import Api.Subscription exposing (Subscription, subscriptionsDecoder)
import Api.User exposing (User)
import Html exposing (Html, button, div, h3, text)
import Html.Attributes exposing (class)
import Html.Events exposing (onClick)
import Http
import RemoteData exposing (WebData)
import View.DeviceViews exposing (deviceList)
import View.ErrorViews exposing (buildErrorMessage, viewFetchError)
import View.UserInfoViews exposing (viewUserInformation)


type alias Model =
    { user : User
    , ownedDevices : WebData (List Device)
    , subscribedDevices : WebData (List Device)
    , pendingSubscriptions : WebData (List Subscription)
    , myPending : WebData (List Device)
    }


type Msg
    = OwnedDevicesReceived (WebData (List Device))
    | SubscribedDevicesReceived (WebData (List Device))
    | MyRequestsReceived (WebData (List Device))
    | PendingSubscritpionsReceived (WebData (List Subscription))
    | DenyPending Subscription
    | ApprovePending Subscription
    | PendingAction (Result Http.Error ())


init : User -> ( Model, Cmd Msg )
init user =
    ( { user = user
      , ownedDevices = RemoteData.Loading
      , subscribedDevices = RemoteData.Loading
      , pendingSubscriptions = RemoteData.NotAsked
      , myPending = RemoteData.NotAsked
      }
    , Cmd.batch
        [ fetchOwnedDevices user
        , fetchSubscribedDevices user
        , fetchPendingSubscriptions user
        , getPendingRequests user
        ]
    )


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


fetchPendingSubscriptions : User -> Cmd Msg
fetchPendingSubscriptions user =
    Http.get
        { url = "http://localhost:8080/iotdevices/rest/subscription/pending/" ++ String.fromInt user.id
        , expect =
            subscriptionsDecoder
                |> Http.expectJson (RemoteData.fromResult >> PendingSubscritpionsReceived)
        }


denySubscription : Subscription -> Cmd Msg
denySubscription sub =
    Http.request
        { method = "POST"
        , body = Http.emptyBody
        , headers = []
        , expect = Http.expectWhatever PendingAction
        , url = "http://localhost:8080/iotdevices/rest/subscription/deny/" ++ String.fromInt sub.device.id ++ "/" ++ String.fromInt sub.user.id
        , timeout = Nothing
        , tracker = Nothing
        }


approveSubscription : Subscription -> Cmd Msg
approveSubscription sub =
    Http.request
        { method = "POST"
        , body = Http.emptyBody
        , headers = []
        , expect = Http.expectWhatever PendingAction
        , url = "http://localhost:8080/iotdevices/rest/subscription/approve/" ++ String.fromInt sub.device.id ++ "/" ++ String.fromInt sub.user.id
        , timeout = Nothing
        , tracker = Nothing
        }


getPendingRequests : User -> Cmd Msg
getPendingRequests user =
    Http.get
        { url = "http://localhost:8080/iotdevices/rest/subscription/mypending/" ++ String.fromInt user.id
        , expect =
            devicesDecoder
                |> Http.expectJson (RemoteData.fromResult >> MyRequestsReceived)
        }


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        OwnedDevicesReceived response ->
            ( { model | ownedDevices = response }, Cmd.none )

        SubscribedDevicesReceived response ->
            ( { model | subscribedDevices = response }, Cmd.none )

        PendingSubscritpionsReceived response ->
            ( { model | pendingSubscriptions = response }, Cmd.none )

        MyRequestsReceived response ->
            ( { model | myPending = response }, Cmd.none )

        DenyPending sub ->
            ( model, denySubscription sub )

        ApprovePending sub ->
            ( model, approveSubscription sub )

        PendingAction _ ->
            ( model, fetchPendingSubscriptions model.user )



-- VIEWS


view : Model -> Html Msg
view model =
    div []
        [ viewUser model.user
        , viewDevices "Owned devices" model.ownedDevices
        , viewDevices "Subscribed devices" model.subscribedDevices
        , viewPending model.pendingSubscriptions
        , viewDevices "My pending subscriptions" model.myPending
        ]


pendingList : String -> List Subscription -> Html Msg
pendingList heading pendingSubscriptions =
    case pendingSubscriptions of
        [] ->
            div []
                [ h3 [] [ text heading ]
                , text "No pending subscriptions"
                ]

        _ ->
            div [ class "deviceList" ]
                [ h3 [] [ text heading ]
                , div []
                    (List.map pendingListItem pendingSubscriptions)
                ]


pendingListItem : Subscription -> Html Msg
pendingListItem pendingSubscription =
    div [ class "deviceListItem noHover" ]
        [ div [ class "deviceListColumn" ] [ text pendingSubscription.device.deviceName ]
        , div [ class "deviceListColumn" ] [ text pendingSubscription.user.username ]
        , button [ class "deviceListButton", onClick (ApprovePending pendingSubscription) ] [ text "APPROVE" ]
        , button [ class "deviceListButton", onClick (DenyPending pendingSubscription) ] [ text "DENY" ]
        ]


viewPending : WebData (List Subscription) -> Html Msg
viewPending pending =
    case pending of
        RemoteData.NotAsked ->
            text ""

        RemoteData.Loading ->
            h3 [] [ text "Loading..." ]

        RemoteData.Success actualPending ->
            pendingList "Pending" actualPending

        RemoteData.Failure httpError ->
            viewFetchError (buildErrorMessage httpError) " pending subscriptions "


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
            viewFetchError (buildErrorMessage httpError) " devices "
