module View.DeviceViews exposing (deviceInformation, deviceList, deviceListItem, feedbackList, feedbackListItem)

import Api.Device exposing (Device)
import Api.Feedback exposing (Feedback)
import Html exposing (Html, a, div, h2, h3, img, span, text)
import Html.Attributes exposing (class, href, src)


deviceList : String -> List Device -> Html msg
deviceList heading devices =
    case devices of
        [] ->
            div []
                [ h3 [] [ text heading ]
                , text "No devices found"
                ]

        _ ->
            div [ class "deviceList" ]
                [ h3 [] [ text heading ]
                , div []
                    (List.map deviceListItem (List.sortBy .deviceName devices))
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
                , feedbackList feedback
                ]
            ]
        ]


feedbackList : List Feedback -> Html msg
feedbackList feedback =
    case feedback of
        [] ->
            text ""

        items ->
            div [] [ h2 [] [ text "Feedback" ], div [ class "feedbackList" ] (List.map (\f -> feedbackListItem f) items) ]


feedbackListItem : Feedback -> Html msg
feedbackListItem feedback =
    div [ class "feedbackListItem" ]
        [ span [] [ text feedback.publishedDate ]
        , span [] [ text (feedback.author.username ++ ":  " ++ feedback.feedbackContent) ]
        ]
