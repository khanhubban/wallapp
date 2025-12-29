package wallapp.process.listener

interface ProcessListener {

    fun <T: Any> onPreferenceChange(key: String, value: T)

}