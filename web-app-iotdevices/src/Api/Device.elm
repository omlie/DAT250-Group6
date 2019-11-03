module Api.Device exposing (..)

import Http
import Json.Decode as Decode exposing (Decoder, int, list, string)
import Json.Decode.Pipeline exposing (required)


type alias Device =
    { id : Int
    , deviceName : String
    , apiUrl : String
    , deviceImg : String
    , status : String
    , statuses : List String
    }


devicesDecoder : Decoder (List Device)
devicesDecoder =
    list deviceDecoder


deviceDecoder : Decoder Device
deviceDecoder =
    Decode.succeed Device
        |> required "id" int
        |> required "deviceName" string
        |> required "apiUrl" string
        |> required "deviceImg" string
        |> required "status" string
        |> required "statuses" (list string)