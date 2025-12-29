package wallapp.interop

interface InteropBridge {
    fun resetAllData()
}

object InteropBridgeNoOp : InteropBridge {
    override fun resetAllData() { }
}

private var interopBridge: InteropBridge? = null
fun registerInteropBridge(bridge: InteropBridge) {
    interopBridge = bridge
}
fun InteropBridge(): InteropBridge = interopBridge!!

