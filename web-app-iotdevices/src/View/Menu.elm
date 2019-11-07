module View.Menu exposing (viewMenu)

import Html exposing (Html, div)
import Html.Attributes exposing (class)
import View.Buttons exposing (viewHrefButton)


menuButtons : List ( String, String )
menuButtons =
    [ ( "My page", "/mypage" ), ( "Devices", "/devices" ), ( "Add device", "/device/add" ) ]


viewMenu : Html msg
viewMenu =
    viewMenuButtons menuButtons


viewMenuButtons : List ( String, String ) -> Html msg
viewMenuButtons buttons =
    div [ class "header" ] (List.map (\( name, ref ) -> viewHrefButton name ref) buttons)
