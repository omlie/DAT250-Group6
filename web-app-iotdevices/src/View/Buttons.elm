module View.Buttons exposing (viewHrefButton)

import Html exposing (Html, a, div, text)
import Html.Attributes exposing (class, href)


viewHrefButton : String -> String -> Html msg
viewHrefButton name ref =
    a [ href ref, class "headerButton" ]
        [ div [] [ text name ] ]
