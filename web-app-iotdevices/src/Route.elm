module Route exposing (Route(..), parseUrl)

import Url exposing (Url)
import Url.Parser exposing ((</>), Parser, int, map, oneOf, parse, s)


type Route
    = NotFound
    | UserInformationPage
    | DeviceListPage
    | NewDevicePage
    | DeviceInformationPage Int
    | LogoutPage


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
        [ map UserInformationPage (s "mypage")
        , map DeviceInformationPage (s "device" </> int)
        , map DeviceListPage (s "devices")
        , map NewDevicePage (s "device" </> s "add")
        , map NotFound (s "not-found")
        , map LogoutPage (s "logout")
        ]
