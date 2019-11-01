module View.UserInfo exposing (..)

import Browser
import Html exposing (..)
import Html.Attributes exposing (class, href)
import Model exposing (User)


userInformation : User -> Html msg
userInformation user =
    div [] [ text (user.firstname ++ " " ++ user.lastname) ]
