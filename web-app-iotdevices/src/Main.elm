module Main exposing (..)

import Browser
import Browser.Navigation as Nav
import Html exposing (Html, span, text)
import Html.Attributes exposing (class)
import Model exposing (..)
import Page.Devices as Devices
import Page.Error as Error
import Page.Index as Index
import Page.MyPage as MyPage
import Route
import Url


init : () -> Url.Url -> Nav.Key -> ( Model, Cmd Msg )
init flags url key =
    let
        page =
            Route.urlToPage url
    in
    ( { key = key
      , url = url
      , page = page
      }
    , Cmd.none
    )


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        LinkClicked urlRequest ->
            case urlRequest of
                Browser.Internal url ->
                    ( model, Nav.pushUrl model.key (Url.toString url) )

                Browser.External href ->
                    ( model, Nav.load href )

        UrlChanged url ->
            ( { model | url = url, page = Route.urlToPage url }, Cmd.none )


view : Model -> Browser.Document Msg
view model =
    case model.page of
        Index ->
            Index.view model

        MyPage ->
            MyPage.view model

        Devices ->
            Devices.view model

        Error ->
            Error.view model


main : Program () Model Msg
main =
    Browser.application
        { init = init
        , view = view
        , update = update
        , subscriptions = always Sub.none
        , onUrlRequest = LinkClicked
        , onUrlChange = UrlChanged
        }
