module View.DeviceViews exposing (..)

import Api.Device exposing (Device)
import Html exposing (..)
import Html.Attributes exposing (class, href)


deviceList : String -> List Device -> Html msg
deviceList heading devices =
    case List.length devices of
        0 ->
            div []
                []

        _ ->
            div
                []
                [ h3 [] [ text heading ]
                , table []
                    (List.map deviceListItem devices)
                ]


deviceListItem : Device -> Html msg
deviceListItem device =
    tr
        [ class "deviceListItem" ]
        [ td []
            [ text device.deviceName ]
        , td []
            [ text device.status ]
        , td []
            [ a [ href ("/device/" ++ String.fromInt device.id) ] [ text " View device >" ] ]
        ]
