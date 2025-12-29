package wallapp.device.memory

import android.app.ActivityManager
import android.content.Context
import wallapp.log.Log


class DeviceMemorySystem(context: Context) : DeviceMemory() {

    override val memory: Long by lazy {
        val result = ActivityManager.MemoryInfo()
        (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getMemoryInfo(result)
        result.totalMem
    }

    init {
        Log.d("init(), memory: $memory, memoryMb: $memoryMb, memoryGb: $memoryGb")
    }
}