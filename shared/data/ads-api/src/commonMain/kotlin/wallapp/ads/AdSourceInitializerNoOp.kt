package wallapp.ads

import wallapp.ads.AdSourceInitializer.State


class AdSourceInitializerNoOp(
) : AdSourceInitializer {

    override val state: State = State.Unsupported

    override fun init() { }
}
