module View.Menu exposing (viewMenu)

import Browser
import Html exposing (..)
import Html.Attributes exposing (class, href)
import View.Buttons exposing (viewHrefButton)


menuButtons : List ( String, String )
menuButtons =
    [ ( "My page", "/mypage" ), ( "Devices", "/devices" ), ( "Index", "/" ) ]


viewMenu : Html msg
viewMenu =
    viewMenuButtons menuButtons


viewMenuButtons : List ( String, String ) -> Html msg
viewMenuButtons buttons =
    buttons
        |> List.map (\( name, ref ) -> viewHrefButton name ref)
        |> div [ class "header" ]
