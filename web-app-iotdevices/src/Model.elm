module Model exposing (Model, Msg(..), Page(..), RequestStatus(..))

import Browser
import Browser.Navigation as Nav
import Url


type alias Model =
    { key : Nav.Key, url : Url.Url, page : Page }


type Msg
    = LinkClicked Browser.UrlRequest
    | UrlChanged Url.Url


type Page
    = Index
    | Error
    | MyPage
    | Devices


type RequestStatus
    = Good
    | InvalidInput
    | ReceivedError
    | NotAsked
