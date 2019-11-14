module Api.DeviceSubscribers exposing (DeviceSubscribers, deviceSubscriberDecoder)

import Api.User exposing (User, userDecoder)
import Json.Decode exposing (Decoder, list)


type alias DeviceSubscribers =
    List User


deviceSubscriberDecoder : Decoder DeviceSubscribers
deviceSubscriberDecoder =
    list userDecoder
