module Main exposing (main)

import Browser exposing (Document, UrlRequest)
import Browser.Navigation as Nav
import Html exposing (Html, div)
import Html.Attributes exposing (class)
import Page.DeviceInformationPage as DeviceInformationPage
import Page.DeviceListPage as DeviceListPage
import Page.ErrorPage as ErrorPage
import Page.NewDevicePage as NewDevicePage
import Page.UserInformationPage as UserInformationPage
import Route exposing (Route)
import Url exposing (Url)
import View.Menu exposing (viewMenu)


type alias Model =
    { route : Route
    , page : Page
    , navKey : Nav.Key
    }


type Page
    = NotFoundPage
    | UserInformationPage UserInformationPage.Model
    | DeviceListPage DeviceListPage.Model
    | DeviceInformationPage DeviceInformationPage.Model
    | NewDevicePage NewDevicePage.Model


main : Program () Model Msg
main =
    Browser.application
        { init = init
        , view = view
        , update = update
        , subscriptions = \_ -> Sub.none
        , onUrlRequest = LinkClicked
        , onUrlChange = UrlChanged
        }


type Msg
    = LinkClicked UrlRequest
    | UrlChanged Url
    | UserInformationPageMsg UserInformationPage.Msg
    | DeviceListPageMsg DeviceListPage.Msg
    | DeviceInformationPageMsg DeviceInformationPage.Msg
    | NewDevicePageMsg NewDevicePage.Msg


init : () -> Url -> Nav.Key -> ( Model, Cmd Msg )
init flags url navKey =
    let
        model =
            { route = Route.parseUrl url
            , page = NotFoundPage
            , navKey = navKey
            }
    in
    initCurrentPage ( model, Cmd.none )


initCurrentPage : ( Model, Cmd Msg ) -> ( Model, Cmd Msg )
initCurrentPage ( model, existingCmds ) =
    let
        ( currentPage, mappedPageCmds ) =
            case model.route of
                Route.NotFound ->
                    ( NotFoundPage, Cmd.none )

                Route.DeviceListPage ->
                    let
                        ( pageModel, pageCmds ) =
                            DeviceListPage.init
                    in
                    ( DeviceListPage pageModel, Cmd.map DeviceListPageMsg pageCmds )

                Route.UserInformationPage ->
                    let
                        ( pageModel, pageCmds ) =
                            UserInformationPage.init
                    in
                    ( UserInformationPage pageModel, Cmd.map UserInformationPageMsg pageCmds )

                Route.NewDevicePage ->
                    let
                        ( pageModel, pageCmds ) =
                            NewDevicePage.init
                    in
                    ( NewDevicePage pageModel, Cmd.map NewDevicePageMsg pageCmds )

                Route.DeviceInformationPage deviceid ->
                    let
                        ( pageModel, pageCmds ) =
                            DeviceInformationPage.init deviceid
                    in
                    ( DeviceInformationPage pageModel, Cmd.map DeviceInformationPageMsg pageCmds )
    in
    ( { model | page = currentPage }
    , Cmd.batch [ existingCmds, mappedPageCmds ]
    )


view : Model -> Document Msg
view model =
    { title = "IOT Devices"
    , body =
        [ div [ class "wrapper" ]
            [ viewMenu
            , div [ class "content" ]
                [ currentView model ]
            ]
        ]
    }


currentView : Model -> Html Msg
currentView model =
    case model.page of
        NotFoundPage ->
            ErrorPage.view

        DeviceListPage pageModel ->
            DeviceListPage.view pageModel
                |> Html.map DeviceListPageMsg

        UserInformationPage pageModel ->
            UserInformationPage.view pageModel
                |> Html.map UserInformationPageMsg

        DeviceInformationPage pageModel ->
            DeviceInformationPage.view pageModel
                |> Html.map DeviceInformationPageMsg

        NewDevicePage pageModel ->
            NewDevicePage.view pageModel
                |> Html.map NewDevicePageMsg


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case ( msg, model.page ) of
        ( UserInformationPageMsg subMsg, UserInformationPage pageModel ) ->
            let
                ( updatedPageModel, updatedCmd ) =
                    UserInformationPage.update subMsg pageModel
            in
            ( { model | page = UserInformationPage updatedPageModel }
            , Cmd.map UserInformationPageMsg updatedCmd
            )

        ( DeviceListPageMsg subMsg, DeviceListPage pageModel ) ->
            let
                ( updatedPageModel, updatedCmd ) =
                    DeviceListPage.update subMsg pageModel
            in
            ( { model | page = DeviceListPage updatedPageModel }
            , Cmd.map DeviceListPageMsg updatedCmd
            )

        ( DeviceInformationPageMsg subMsg, DeviceInformationPage pageModel ) ->
            let
                ( updatedPageModel, updatedCmd ) =
                    DeviceInformationPage.update subMsg pageModel
            in
            ( { model | page = DeviceInformationPage updatedPageModel }
            , Cmd.map DeviceInformationPageMsg updatedCmd
            )

        ( NewDevicePageMsg subMsg, NewDevicePage pageModel ) ->
            let
                ( updatedPageModel, updatedCmd ) =
                    NewDevicePage.update subMsg pageModel
            in
            ( { model | page = NewDevicePage updatedPageModel }
            , Cmd.map NewDevicePageMsg updatedCmd
            )

        ( LinkClicked urlRequest, _ ) ->
            case urlRequest of
                Browser.Internal url ->
                    ( model
                    , Nav.pushUrl model.navKey (Url.toString url)
                    )

                Browser.External url ->
                    ( model
                    , Nav.load url
                    )

        ( UrlChanged url, _ ) ->
            let
                newRoute =
                    Route.parseUrl url
            in
            ( { model | route = newRoute }, Cmd.none )
                |> initCurrentPage

        ( _, _ ) ->
            ( model, Cmd.none )
