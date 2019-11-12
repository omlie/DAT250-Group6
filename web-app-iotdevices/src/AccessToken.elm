port module AccessToken exposing (deleteToken, setToken)

import Json.Encode exposing (Value)


port setToken : Value -> Cmd msg


port deleteToken : Value -> Cmd msg
