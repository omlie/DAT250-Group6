module Api.Device exposing (..)

import Http
import Json.Decode as Decode exposing (Decoder, int, list, string)
import Json.Decode.Pipeline exposing (required)


type alias Device =
    { id : Int
    , name : String
    , api : String
    , status : String
    , image : String
    }


devicesDecoder : Decoder (List Device)
devicesDecoder =
    list deviceDecoder


deviceDecoder : Decoder Device
deviceDecoder =
    Decode.succeed Device
        |> required "id" int
        |> required "deviceName" string
        |> required "api" string
        |> required "image" string
        |> required "status" string
