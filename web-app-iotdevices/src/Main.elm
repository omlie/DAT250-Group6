module Main exposing (main)

import Browser exposing (Document, UrlRequest)
import Browser.Navigation as Nav
import Html exposing (..)
import Page.DeviceListPage as DeviceListPage
import Page.ErrorPage as ErrorPage
import Page.IndexPage as IndexPage
import Page.UserInformationPage as UserInformationPage
import Route exposing (Route)
import Url exposing (Url)


type alias Model =
    { route : Route
    , page : Page
    , navKey : Nav.Key
    }


type Page
    = NotFoundPage
    | UserInformationPage UserInformationPage.Model
    | IndexPage
    | DeviceListPage DeviceListPage.Model


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

                Route.IndexPage ->
                    ( IndexPage, Cmd.none )

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
    in
    ( { model | page = currentPage }
    , Cmd.batch [ existingCmds, mappedPageCmds ]
    )


view : Model -> Document Msg
view model =
    { title = "Post App"
    , body = [ currentView model ]
    }


currentView : Model -> Html Msg
currentView model =
    case model.page of
        NotFoundPage ->
            ErrorPage.view

        DeviceListPage pageModel ->
            DeviceListPage.view pageModel
                |> Html.map DeviceListPageMsg

        IndexPage ->
            IndexPage.view

        UserInformationPage pageModel ->
            UserInformationPage.view pageModel
                |> Html.map UserInformationPageMsg


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
