module Page.UserInformationPage exposing (Model, Msg, init, update, view)

import Api.User exposing (User, userDecoder)
import Html exposing (..)
import Html.Attributes exposing (..)
import Html.Events exposing (onClick)
import Http
import Json.Decode as Decode
import RemoteData exposing (WebData)
import View.Menu exposing (viewMenu)


type alias Model =
    { user : WebData User
    }


type Msg
    = FetchUser
    | UserReceived (WebData User)


init : ( Model, Cmd Msg )
init =
    ( { user = RemoteData.Loading }, fetchUser )


fetchUser : Cmd Msg
fetchUser =
    Http.get
        { url = "http://localhost:8080/iotdevices/rest/users/1"
        , expect =
            userDecoder
                |> Http.expectJson (RemoteData.fromResult >> UserReceived)
        }


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        FetchUser ->
            ( { model | user = RemoteData.Loading }, fetchUser )

        UserReceived response ->
            ( { model | user = response }, Cmd.none )



-- VIEWS


view : Model -> Html Msg
view model =
    div [ class "wrapper" ]
        [ viewMenu
        , div [ class "content" ] [ viewUser model.user ]
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


viewFetchError : String -> Html Msg
viewFetchError errorMessage =
    let
        errorHeading =
            "Couldn't fetch User at this time."
    in
    div []
        [ h3 [] [ text errorHeading ]
        , text ("Error: " ++ errorMessage)
        ]


buildErrorMessage : Http.Error -> String
buildErrorMessage httpError =
    case httpError of
        Http.BadUrl message ->
            message

        Http.Timeout ->
            "Server is taking too long to respond. Please try again later."

        Http.NetworkError ->
            "Unable to reach server."

        Http.BadStatus statusCode ->
            "Request failed with status code: " ++ String.fromInt statusCode

        Http.BadBody message ->
            message
