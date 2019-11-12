module Page.LogoutPage exposing (Model, Msg, init, update, view)

import AccessToken exposing (deleteToken)
import Browser.Navigation exposing (load)
import Html exposing (Html, button, div, h2, text)
import Html.Attributes exposing (class)
import Html.Events exposing (onClick)
import Json.Encode exposing (string)


type alias Model =
    {}


type Msg
    = Logout
    | DontLogout


init : ( Model, Cmd Msg )
init =
    ( {}
    , Cmd.none
    )


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        Logout ->
            ( model, Cmd.batch [ deleteToken (string "token"), redirect True ] )

        DontLogout ->
            ( model, redirect False )


redirect : Bool -> Cmd Msg
redirect logout =
    if logout then
        load "http://localhost:8000/"

    else
        load "http://localhost:8000/mypage"



-- VIEWS


view : Model -> Html Msg
view _ =
    div []
        [ h2 []
            [ text "Are you sure you want to logout?" ]
        , div
            [ class "buttonrow" ]
            [ button [ class "submitbutton", onClick Logout ] [ text "Logout" ]
            , button [ class "submitbutton", onClick DontLogout ] [ text "Don't logout" ]
            ]
        ]
