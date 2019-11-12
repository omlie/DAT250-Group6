module Api.Subscription exposing (Subscription, subscriptionDecoder, subscriptionsDecoder, subscriptionStatusDecoder, SubscriptionStatus)

import Api.Device exposing (Device, deviceDecoder)
import Api.User exposing (User, userDecoder)
import Json.Decode as Decode exposing (Decoder, int, list, bool, string)
import Json.Decode.Pipeline exposing (required)

type alias Subscription =
    { id : Int
    , user : User
    , device : Device
    , isApproved : Bool
    , isDenied : Bool
    }

type alias SubscriptionStatus = { status : String}


subscriptionsDecoder : Decoder (List Subscription)
subscriptionsDecoder =
    list subscriptionDecoder


subscriptionDecoder : Decoder Subscription
subscriptionDecoder =
    Decode.succeed Subscription
        |> required "id" int
        |> required "user" userDecoder
        |> required "device" deviceDecoder
        |> required "approvedSubscription" bool
        |> required "deniedSubscription" bool

subscriptionStatusDecoder : Decoder SubscriptionStatus
subscriptionStatusDecoder =
    Decode.succeed SubscriptionStatus
        |> required "subscriptionStatus" string