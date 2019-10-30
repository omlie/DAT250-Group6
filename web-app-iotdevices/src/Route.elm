module Route exposing (urlParser, urlToPage)

import Model exposing (Page(..), RequestStatus(..))
import Url exposing (Url)
import Url.Parser as Url exposing ((</>), Parser, string)


urlToPage : Url -> Page
urlToPage url =
    url
        |> Url.parse urlParser
        |> Maybe.withDefault Index


urlParser : Parser (Page -> a) a
urlParser =
    Url.oneOf
        [ Url.map Index Url.top
        , Url.map MyPage (Url.s "mypage")
        , Url.map Devices (Url.s "devices")
        , Url.map Error (Url.s "not-found")
        ]
