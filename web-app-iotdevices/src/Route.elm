module Route exposing (Route(..), parseUrl)

import Url exposing (Url)
import Url.Parser exposing (..)


type Route
    = NotFound
    | UserInformationPage
    | IndexPage
    | DeviceListPage


parseUrl : Url -> Route
parseUrl url =
    case parse matchRoute url of
        Just route ->
            route

        Nothing ->
            NotFound


matchRoute : Parser (Route -> a) a
matchRoute =
    oneOf
        [ map IndexPage top
        , map UserInformationPage (s "mypage")
        , map DeviceListPage (s "devices")
        , map NotFound (s "not-found")
        ]
