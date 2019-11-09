module Main exposing (main)

import Api.User exposing (User, userDecoder)
import Browser exposing (Document, UrlRequest)
import Browser.Navigation as Nav
import Html exposing (Html, button, div, input, text)
import Html.Attributes exposing (class, placeholder, type_, value)
import Html.Events exposing (onClick, onInput)
import Http
import Page.DeviceInformationPage as DeviceInformationPage
import Page.DeviceListPage as DeviceListPage
import Page.ErrorPage as ErrorPage
import Page.LoginPage as LoginPage
import Page.NewDevicePage as NewDevicePage
import Page.UserInformationPage as UserInformationPage
import RemoteData exposing (WebData)
import Route exposing (Route)
import Url exposing (Url)
import View.Menu exposing (viewMenu)


type alias Model =
    { route : Route
    , page : Page
    , navKey : Nav.Key
    , loggedIn : Bool
    , username : String
    , password : String
    , user : User
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
    | UsernameChange String
    | PasswordChange String
    | TryLogin
    | LoginSucceded (WebData User)


init : () -> Url -> Nav.Key -> ( Model, Cmd Msg )
init flags url navKey =
    let
        model =
            { route = Route.parseUrl url
            , page = NotFoundPage
            , navKey = navKey
            , loggedIn = False
            , username = ""
            , password = ""
            , user = { id = -1, username = "", firstname = "", lastname = "" }
            }
    in
    initCurrentPage ( model, Cmd.none )


initCurrentPage : ( Model, Cmd Msg ) -> ( Model, Cmd Msg )
initCurrentPage ( model, existingCmds ) =
    if model.loggedIn then
        let
            ( currentPage, mappedPageCmds ) =
                case model.route of
                    Route.NotFound ->
                        ( NotFoundPage, Cmd.none )

                    Route.DeviceListPage ->
                        let
                            ( pageModel, pageCmds ) =
                                DeviceListPage.init model.user
                        in
                        ( DeviceListPage pageModel, Cmd.map DeviceListPageMsg pageCmds )

                    Route.UserInformationPage ->
                        let
                            ( pageModel, pageCmds ) =
                                UserInformationPage.init model.user
                        in
                        ( UserInformationPage pageModel, Cmd.map UserInformationPageMsg pageCmds )

                    Route.NewDevicePage ->
                        let
                            ( pageModel, pageCmds ) =
                                NewDevicePage.init model.navKey model.user
                        in
                        ( NewDevicePage pageModel, Cmd.map NewDevicePageMsg pageCmds )

                    Route.DeviceInformationPage deviceid ->
                        let
                            ( pageModel, pageCmds ) =
                                DeviceInformationPage.init model.user deviceid
                        in
                        ( DeviceInformationPage pageModel, Cmd.map DeviceInformationPageMsg pageCmds )
        in
        ( { model | page = currentPage }
        , Cmd.batch [ existingCmds, mappedPageCmds ]
        )

    else
        ( model, existingCmds )


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
    if model.loggedIn then
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

    else
        div [ class "form" ]
            [ input [ placeholder "Username", model.username |> value, onInput UsernameChange ] []
            , input [ placeholder "Password", type_ "password", model.password |> value, onInput PasswordChange ] []
            , button [ class "submitbutton", onClick TryLogin ] [ text "Login" ]
            ]


login : Model -> Cmd Msg
login model =
    Http.request
        { body = Http.emptyBody
        , method = "POST"
        , headers = [ Http.header "username" model.username, Http.header "password" model.password ]
        , expect =
            userDecoder
                |> Http.expectJson (RemoteData.fromResult >> LoginSucceded)
        , url = "http://localhost:8080/iotdevices/rest/users/login"
        , timeout = Nothing
        , tracker = Nothing
        }


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

        ( TryLogin, _ ) ->
            ( model, login model )

        ( LoginSucceded user, _ ) ->
            case user of
                RemoteData.Success actualuser ->
                    ( { model | username = "", password = "", loggedIn = True, route = Route.UserInformationPage, user = actualuser }, Cmd.none )
                        |> initCurrentPage

                _ ->
                    ( model, Cmd.none )

        ( UsernameChange username, _ ) ->
            ( { model | username = username }, Cmd.none )

        ( PasswordChange password, _ ) ->
            ( { model | password = password }, Cmd.none )

        ( _, _ ) ->
            ( model, Cmd.none )
