package wallapp.auth.google

data class GoogleAuthData(
    val idToken: String?,
    val accessToken: String?,
    val name: String?,
    val photoUrl: String?,
) {

    companion object {
        val Preset = GoogleAuthData(
            idToken = "testIdToken",
            accessToken = "testAccessToken",
            name = "testName",
            photoUrl = null,
        )

        val PresetIos = GoogleAuthData(
            idToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6Ijg1YmE5MzEzZmQ3YTdkNGFmYTg0ODg0YWJjYzg0MDMwMDQzNjMxODAiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiI1ODk0NTM5MTcwMzgtcWFvZ2E4OWZpdGoydWtyc3EyN2tvNTZmaW1tb2phYzYuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiI1ODk0NTM5MTcwMzgtcWFvZ2E4OWZpdGoydWtyc3EyN2tvNTZmaW1tb2phYzYuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMDY1ODk1ODc5ODMwNjIzMjU5NTYiLCJlbWFpbCI6ImxhY3kubXVmY0BnbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiYXRfaGFzaCI6IkJ3dGE0ekRqUTJCaG1aeXc4SUQxT1EiLCJub25jZSI6Imkwc3dHUzhTWGQxaXIzN3RQX3VVYTZ1OTJxbEFNaTB2Z21CaHpwX2djMmsiLCJuYW1lIjoiQ2hyaXMgTGFjeSIsInBpY3R1cmUiOiJodHRwczovL2xoMy5nb29nbGV1c2VyY29udGVudC5jb20vYS9BQWNIVHRjT0ZTUFZMc014bFowaVBRbHdkTldEa2dHYlhpVVQzZ0RiR01JRT1zOTYtYyIsImdpdmVuX25hbWUiOiJDaHJpcyIsImZhbWlseV9uYW1lIjoiTGFjeSIsImxvY2FsZSI6ImVuIiwiaWF0IjoxNjg2NjIxMzQ1LCJleHAiOjE2ODY2MjQ5NDV9.PE1FRK7qDYABo38RZYbEYZJdtTBFsL-KO5ZEiVOtc69-29BBoAdUOKeY5oUp33RTVfmjhXL3HfFFTKECRPCXFZ2dTtyOgPahhFHl63PpEfW4s3YBPPxhBJNHcqIhKssKh9qJt7LBXIf0cdo2n-4lC1DJ7HzLViBJR7F4olg1tWg2RYgc9aeY8lN9_P7OatdkV6r9Rzbh5JnTdlaPJTeD0xG4zKQ9q2jn7lsIqNaUrZrjeZ1B12CY0L2waVxJL3N3hmxEWuPhhfH65UaawXwBJkdhFkWMwcli6UszmRIV5BL_1f0mvl4lklIjiJb9w7Q-_aKIoHk3B_sdZ8G9eA9kBQ",
            accessToken = "ya29.a0AWY7CknwldOhqrrmUJhca4EBzEwwDRybY_d5kZyFxzhCb7R2SWpY-_IAS0qsbC1tSsjAdbvfpJY6jXz5_6en-PPH4NT55O5YxrJ_OiU-Q5T7g9s-lXZT2booFMFw6zWEhfT0uBs9HHHiguDQPUWrzThOeml-aCgYKAc0SARESFQG1tDrpA9f3yvOkTc6D1WRvM41vww0163",
            name = "testName",
            photoUrl = null,
        )
    }
}
