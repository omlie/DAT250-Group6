module View.UserInfoViews exposing (viewUserInformation)

import Api.User exposing (User)
import Html exposing (Html, div, text)


viewUserInformation : User -> Html msg
viewUserInformation user =
    div [] [ text ("Hello " ++ user.firstname ++ " " ++ user.lastname ++ "!") ]
