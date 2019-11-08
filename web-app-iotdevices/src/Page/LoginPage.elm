module Page.LoginPage exposing (Model, Msg, init, update, view)

import Api.User exposing (User, userDecoder)
import Browser.Navigation exposing (load)
import Html exposing (Html, button, div, input, text)
import Html.Attributes exposing (class, placeholder, type_, value)
import Html.Events exposing (onClick, onInput)
import Http
import RemoteData exposing (WebData)


type alias Model =
    { username : String
    , password : String
    }


type Msg
    = UserNameChange String
    | PasswordChange String
    | AttemptLogin
    | LoginAttempted (WebData User)


init : ( Model, Cmd Msg )
init =
    ( { username = ""
      , password = ""
      }
    , Cmd.none
    )


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        UserNameChange username ->
            ( { model | username = username }, Cmd.none )

        PasswordChange password ->
            ( { model | password = password }, Cmd.none )

        AttemptLogin ->
            ( model, login model )

        LoginAttempted response ->
            ( model, redirect response )


redirect : WebData User -> Cmd Msg
redirect user =
    case user of
        RemoteData.Success _ ->
            load "http://localhost:8000/mypage/"

        _ ->
            load "http://localhost:8000/"


login : Model -> Cmd Msg
login model =
    Http.request
        { body = Http.emptyBody
        , method = "POST"
        , headers =
            [ Http.header "username" model.username
            , Http.header "password" model.password
            ]
        , expect =
            userDecoder
                |> Http.expectJson (RemoteData.fromResult >> LoginAttempted)
        , url = "http://localhost:8080/iotdevices/rest/users/login"
        , timeout = Nothing
        , tracker = Nothing
        }



-- VIEWS


view : Model -> Html Msg
view model =
    viewLoginForm model


viewLoginForm : Model -> Html Msg
viewLoginForm model =
    div [ class "form" ]
        [ input [ placeholder "Username", model.username |> value, onInput UserNameChange ] []
        , input [ type_ "password", placeholder "Password", model.password |> value, onInput PasswordChange ] []
        , button [ class "submitbutton", onClick AttemptLogin ] [ text "Login" ]
        ]
