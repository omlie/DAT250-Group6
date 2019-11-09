module View.Menu exposing (viewMenu)

import Html exposing (Html, div, text, span)
import Html.Attributes exposing (class)
import View.Buttons exposing (viewHrefButton)


menuButtons : List ( String, String )
menuButtons =
    [ ( "My page", "/mypage" ), ( "Devices", "/devices" ), ( "Add device", "/device/add" ) ]


viewMenu : Bool -> Html msg
viewMenu loggedIn =
    if loggedIn then
        viewMenuButtons menuButtons

    else
        div [ class "header" ] [ span [] [ text "IOT Devices" ] ]


viewMenuButtons : List ( String, String ) -> Html msg
viewMenuButtons buttons =
    div [ class "header" ] (List.map (\( name, ref ) -> viewHrefButton name ref) buttons)
