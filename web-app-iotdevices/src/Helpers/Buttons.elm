module Helpers.Buttons exposing (..)

import Browser
import Html exposing (..)
import Html.Attributes exposing (class, href)


viewHrefButton : String -> String -> Html msg
viewHrefButton name ref =
    a [ href ref ]
        [ button [] [ text name ] ]
