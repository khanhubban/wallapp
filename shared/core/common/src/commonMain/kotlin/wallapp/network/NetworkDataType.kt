package wallapp.network

sealed class NetworkDataType {
    data object UserSignIn : NetworkDataType()
    data object Key : NetworkDataType()
    data object RemoteEndpoints : NetworkDataType()
    data object NetworkContent : NetworkDataType()
    data object MediaMap : NetworkDataType()
    data object SearchContent : NetworkDataType()
}