module Page.MyPage exposing (view)

import Browser
import Helpers.View exposing (viewButton)
import Html exposing (..)
import Html.Attributes exposing (class, href)
import Model exposing (..)


view : Model -> Browser.Document msg
view model =
    { title = "My Page"
    , body =
        [ div []
            [ div [] [ text "My page" ]
            , div [] [ viewButton "404" "/not-found" ]
            , div [] [ viewButton "My page" "/mypage" ]
            , div [] [ viewButton "Devices" "/devices" ]
            , div [] [ viewButton "Index" "/" ]
            ]
        ]
    }
