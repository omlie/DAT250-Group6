module Helpers.Menu exposing (viewMenu)

import Browser
import Helpers.Buttons exposing (viewHrefButton)
import Html exposing (..)
import Html.Attributes exposing (class, href)


menuButtons : List ( String, String )
menuButtons =
    [ ( "My page", "/mypage" ), ( "Devices", "/devices" ), ( "404", "/not-found" ), ( "Index", "/" ) ]


viewMenu : Html msg
viewMenu =
    div []
        [ viewMenuButtons menuButtons ]


viewMenuButtons : List ( String, String ) -> Html msg
viewMenuButtons buttons =
    buttons
        |> List.map (\( name, ref ) -> viewHrefButton name ref)
        |> div []
