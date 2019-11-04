module Api.Feedback exposing (..)

import Api.User exposing (User, userDecoder)
import Http
import Json.Decode as Decode exposing (Decoder, int, list, string)
import Json.Decode.Pipeline exposing (required)


type alias Feedback =
    { id : Int
    , author : User
    , feedbackContent : String
    , publishedDate : String
    }


feedbackListDecoder : Decoder (List Feedback)
feedbackListDecoder =
    list feedbackDecoder


feedbackDecoder : Decoder Feedback
feedbackDecoder =
    Decode.succeed Feedback
        |> required "id" int
        |> required "author" userDecoder
        |> required "feedbackContent" string
        |> required "publishedDate" string
