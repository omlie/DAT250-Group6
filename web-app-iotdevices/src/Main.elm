module Main exposing (main)

import AccessToken exposing (setToken)
import Api.User exposing (User, userDecoder)
import Browser exposing (Document, UrlRequest)
import Browser.Navigation as Nav
import Html exposing (Html, button, div, input, span, text)
import Html.Attributes exposing (class, placeholder, type_, value)
import Html.Events exposing (onClick, onInput)
import Http
import Json.Encode exposing (Value, int, object, string)
import Page.DeviceInformationPage as DeviceInformationPage
import Page.DeviceListPage as DeviceListPage
import Page.ErrorPage as ErrorPage
import Page.LogoutPage as LogoutPage
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
    , register : Bool
    , username : String
    , firstname : String
    , lastname : String
    , password : String
    , user : User
    , url : Url
    }


type Page
    = NotFoundPage
    | UserInformationPage UserInformationPage.Model
    | DeviceListPage DeviceListPage.Model
    | DeviceInformationPage DeviceInformationPage.Model
    | NewDevicePage NewDevicePage.Model
    | LogoutPage LogoutPage.Model


main : Program Int Model Msg
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
    | LogoutPageMsg LogoutPage.Msg
    | UsernameChange String
    | FirstnameChange String
    | LastnameChange String
    | PasswordChange String
    | Register Bool
    | TryLogin
    | TryRegister
    | LoginSucceded (WebData User)
    | RegisterSucceded (WebData User)


init : Int -> Url -> Nav.Key -> ( Model, Cmd Msg )
init userid url navKey =
    let
        loggedin =
            userid /= -1
    in
    let
        model =
            { route = Route.parseUrl url
            , page = NotFoundPage
            , navKey = navKey
            , loggedIn = loggedin
            , register = False
            , username = ""
            , firstname = ""
            , lastname = ""
            , password = ""
            , user = { id = userid, username = "", firstname = "", lastname = "" }
            , url = url
            }
    in
    initCurrentPage
        ( model
        , if model.loggedIn then
            getUser userid

          else
            Cmd.none
        )


initCurrentPage : ( Model, Cmd Msg ) -> ( Model, Cmd Msg )
initCurrentPage ( model, existingCmds ) =
    if model.loggedIn then
        let
            ( currentPage, mappedPageCmds ) =
                case model.route of
                    Route.NotFound ->
                        ( NotFoundPage, Cmd.none )

                    Route.LogoutPage ->
                        let
                            ( pageModel, pageCmds ) =
                                LogoutPage.init
                        in
                        ( LogoutPage pageModel, Cmd.map LogoutPageMsg pageCmds )

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
                                NewDevicePage.init model.navKey model.user Nothing
                        in
                        ( NewDevicePage pageModel, Cmd.map NewDevicePageMsg pageCmds )

                    Route.DeviceInformationPage deviceid ->
                        let
                            ( pageModel, pageCmds ) =
                                DeviceInformationPage.init model.user deviceid
                        in
                        ( DeviceInformationPage pageModel, Cmd.map DeviceInformationPageMsg pageCmds )

                    Route.EditDevicePage deviceId ->
                        let
                            ( pageModel, pageCmds ) =
                                NewDevicePage.init model.navKey model.user (Just deviceId)
                        in
                        ( NewDevicePage pageModel, Cmd.map NewDevicePageMsg pageCmds )
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
            [ viewMenu model.loggedIn
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

            LogoutPage pageModel ->
                LogoutPage.view pageModel
                    |> Html.map LogoutPageMsg

    else if model.register then
        div [ class "form" ]
            [ span []
                [ input
                    [ placeholder "Username"
                    , model.username |> value
                    , onInput UsernameChange
                    ]
                    []
                ]
            , span []
                [ input
                    [ placeholder "First name"
                    , model.firstname |> value
                    , onInput FirstnameChange
                    ]
                    []
                ]
            , span []
                [ input
                    [ placeholder "Last name"
                    , model.lastname |> value
                    , onInput LastnameChange
                    ]
                    []
                ]
            , span []
                [ input
                    [ placeholder "Password"
                    , type_ "password"
                    , model.password |> value
                    , onInput PasswordChange
                    ]
                    []
                ]
            , div [ class "buttonrow" ]
                [ button [ class "submitbutton", onClick TryRegister ] [ text "Register" ]
                , button [ class "submitbutton", onClick (Register False) ] [ text "Login" ]
                ]
            ]

    else
        div [ class "form" ]
            [ span []
                [ input
                    [ placeholder "Username"
                    , model.username |> value
                    , onInput UsernameChange
                    ]
                    []
                ]
            , span []
                [ input
                    [ placeholder "Password"
                    , type_ "password"
                    , model.password |> value
                    , onInput PasswordChange
                    ]
                    []
                ]
            , div [ class "buttonrow" ]
                [ button [ class "submitbutton", onClick TryLogin ] [ text "Login" ]
                , button [ class "submitbutton", onClick (Register True) ] [ text "Register" ]
                ]
            ]


getUser : Int -> Cmd Msg
getUser userid =
    Http.get
        { url = "http://localhost:8080/iotdevices/rest/users/" ++ String.fromInt userid
        , expect =
            userDecoder
                |> Http.expectJson (RemoteData.fromResult >> LoginSucceded)
        }


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


register : Model -> Cmd Msg
register model =
    Http.request
        { body = Http.jsonBody (encodeUser model)
        , method = "POST"
        , headers = []
        , expect =
            userDecoder
                |> Http.expectJson (RemoteData.fromResult >> RegisterSucceded)
        , url = "http://localhost:8080/iotdevices/rest/users/register"
        , timeout = Nothing
        , tracker = Nothing
        }


encodeUser : Model -> Value
encodeUser model =
    let
        bodylist =
            [ ( "username", string model.username )
            , ( "firstname", string model.firstname )
            , ( "lastname", string model.lastname )
            , ( "password", string model.password )
            ]
    in
    bodylist
        |> object


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case ( msg, model.page ) of
        ( LogoutPageMsg subMsg, LogoutPage pageModel ) ->
            let
                ( updatedPageModel, updatedCmd ) =
                    LogoutPage.update subMsg pageModel
            in
            ( { model | page = LogoutPage updatedPageModel }
            , Cmd.map LogoutPageMsg updatedCmd
            )

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

        ( Register bool, _ ) ->
            ( { model | register = bool }, Cmd.none )

        ( TryLogin, _ ) ->
            ( model, login model )

        ( TryRegister, _ ) ->
            ( model, register model )

        ( LoginSucceded user, _ ) ->
            case user of
                RemoteData.Success actualuser ->
                    ( { model
                        | username = ""
                        , password = ""
                        , loggedIn = True
                        , route = Route.parseUrl model.url
                        , user = actualuser
                      }
                    , setToken (int actualuser.id)
                    )
                        |> initCurrentPage

                _ ->
                    ( model, Cmd.none )

        ( RegisterSucceded user, _ ) ->
            case user of
                RemoteData.Success actualuser ->
                    ( { model
                        | username = ""
                        , password = ""
                        , loggedIn = True
                        , route = Route.parseUrl model.url
                        , user = actualuser
                      }
                    , setToken (int actualuser.id)
                    )
                        |> initCurrentPage

                _ ->
                    ( model, Cmd.none )

        ( UsernameChange name, _ ) ->
            ( { model | username = name }, Cmd.none )

        ( FirstnameChange name, _ ) ->
            ( { model | firstname = name }, Cmd.none )

        ( LastnameChange name, _ ) ->
            ( { model | lastname = name }, Cmd.none )

        ( PasswordChange password, _ ) ->
            ( { model | password = password }, Cmd.none )

        ( _, _ ) ->
            ( model, Cmd.none )
