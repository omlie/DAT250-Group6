module Page.Index exposing (view)

import Browser
import Helpers.Menu exposing (viewMenu)
import Html exposing (..)
import Html.Attributes exposing (class, href)
import Model exposing (..)


view : Model -> Browser.Document msg
view model =
    { title = "Index"
    , body =
        [ div []
            [ viewMenu ]
        ]
    }
