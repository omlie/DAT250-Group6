module Route exposing (Route(..), parseUrl)

import Url exposing (Url)
import Url.Parser exposing ((</>), Parser, int, map, oneOf, parse, s, top)


type Route
    = NotFound
    | UserInformationPage
    | DeviceListPage
    | NewDevicePage
    | EditDevicePage Int
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
        [ map UserInformationPage top
        , map UserInformationPage (s "mypage")
        , map DeviceInformationPage (s "device" </> int)
        , map DeviceListPage (s "devices")
        , map NewDevicePage (s "device" </> s "add")
        , map EditDevicePage (s "device" </> s "edit" </> int)
        , map NotFound (s "not-found")
        , map LogoutPage (s "logout")
        ]
