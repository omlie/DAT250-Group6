module Page.MyPage exposing (view)

import Browser
import Helpers.Menu exposing (viewMenu)
import Html exposing (..)
import Html.Attributes exposing (class, href)
import Model exposing (..)


view : Model -> Browser.Document msg
view model =
    { title = "My Page"
    , body =
        [ div []
            [ viewMenu ]
        ]
    }
