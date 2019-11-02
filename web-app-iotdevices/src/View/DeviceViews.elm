module View.DeviceViews exposing (..)

import Api.Device exposing (Device)
import Api.Feedback exposing (Feedback)
import Html exposing (..)
import Html.Attributes exposing (class, href, src)


deviceList : String -> List Device -> Html msg
deviceList heading devices =
    case List.length devices of
        0 ->
            div [] []

        _ ->
            div [ class "deviceList" ]
                [ h3 [] [ text heading ]
                , div []
                    (List.map deviceListItem devices)
                ]


deviceListItem : Device -> Html msg
deviceListItem device =
    a [ href ("/device/" ++ String.fromInt device.id) ]
        [ div
            [ class "deviceListItem" ]
            [ div [ class "deviceListColumn" ] [ text device.deviceName ]
            , div [ class "deviceListColumn" ] [ text device.status ]
            ]
        ]


deviceInformation : Device -> List Feedback -> Html msg
deviceInformation device feedback =
    div [ class "content" ]
        [ div [ class "deviceInformationWrapper" ]
            [ img [ class "deviceImage", src device.deviceImg ] []
            , div [ class "deviceInformation" ]
                [ h2 [] [ text device.deviceName ]
                , span [] [ text device.status ]
                , span [] [ text ("API URL: " ++ device.apiUrl) ]
                , div [] [ h2 [] [ text "Feedback" ], div [] (List.map (\f -> div [] [ text f.feedbackContent ]) feedback) ]
                ]
            ]
        ]
