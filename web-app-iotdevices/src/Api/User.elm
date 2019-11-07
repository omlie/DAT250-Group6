module Api.User exposing (User, userDecoder, usersDecoder)

import Json.Decode as Decode exposing (Decoder, int, list, string)
import Json.Decode.Pipeline exposing (required)


type alias User =
    { id : Int
    , username : String
    , firstname : String
    , lastname : String
    }


usersDecoder : Decoder (List User)
usersDecoder =
    list userDecoder


userDecoder : Decoder User
userDecoder =
    Decode.succeed User
        |> required "id" int
        |> required "userName" string
        |> required "firstName" string
        |> required "lastName" string
