module Route exposing (Route(..), parseUrl)

import Url exposing (Url)
import Url.Parser exposing (..)


type Route
    = NotFound
    | UserInformationPage
    | DeviceListPage
    | DeviceInformationPage Int


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
        [ map UserInformationPage top
        , map UserInformationPage (s "mypage")
        , map DeviceInformationPage (s "device" </> int)
        , map DeviceListPage (s "devices")
        , map NotFound (s "not-found")
        ]
