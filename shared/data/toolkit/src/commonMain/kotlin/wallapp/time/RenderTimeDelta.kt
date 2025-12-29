package wallapp.time

import wallapp.device.refreshrate.DeviceRefreshRate

interface RenderTimeDelta {

    val timeDelta: Double

}


class RenderTimeDeltaFrameInterval(val deviceRefreshRate: DeviceRefreshRate)
    : RenderTimeDelta {

    val frameInterval: Double
        get() = 1000.0 / deviceRefreshRate.currentRefreshRate.value!!.toDouble()

    override val timeDelta: Double
        get() = frameInterval

}


class RenderTimeDeltaMock(var mockTimeDelta: Double = 0.0): RenderTimeDelta {

    override val timeDelta: Double
        get() = mockTimeDelta

}