module Page.IndexPage exposing (view)

import Browser
import Html exposing (..)
import Html.Attributes exposing (class, href)
import View.Menu exposing (viewMenu)


view : Html msg
view =
    div [ class "wrapper" ]
        [ viewMenu
        , div [ class "content" ] [ text "hello" ]
        ]
