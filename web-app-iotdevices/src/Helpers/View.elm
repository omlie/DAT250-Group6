module Helpers.View exposing (..)

import Browser
import Html exposing (..)
import Html.Attributes exposing (class, href)


viewButton : String -> String -> Html msg
viewButton name ref =
    a [ href ref ]
        [ button
            [ class "button btn big-button" ]
            [ text name ]
        ]
