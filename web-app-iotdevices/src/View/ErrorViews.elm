module View.ErrorViews exposing (buildErrorMessage, viewFetchError)

import Html exposing (Html, div, h3, text)
import Http


viewFetchError : String -> String -> Html msg
viewFetchError errorMessage attempted =
    let
        errorHeading =
            "Couldn't fetch" ++ attempted ++ " at this time."
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
