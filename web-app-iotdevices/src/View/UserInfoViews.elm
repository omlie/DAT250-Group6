module View.UserInfoViews exposing (..)

import Api.User exposing (User)
import Browser
import Html exposing (..)
import Html.Attributes exposing (class, href)


viewUserInformation : User -> Html msg
viewUserInformation user =
    div [] [ text ("Hello " ++ user.firstname ++ " " ++ user.lastname ++ "!") ]
