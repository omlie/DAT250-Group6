module Page.Error exposing (view)

import Browser
import Helpers.Menu exposing (viewMenu)
import Html exposing (..)
import Html.Attributes exposing (class, href)
import Model exposing (..)


view : Model -> Browser.Document msg
view model =
    { title = "404"
    , body =
        [ div []
            [ viewMenu ]
        ]
    }
