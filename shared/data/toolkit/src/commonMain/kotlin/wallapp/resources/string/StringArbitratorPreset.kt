package wallapp.resources.string

import wallapp.buildconfig.BuildConfigMock
import wallapp.device.DeviceCountryMock
import wallapp.time.TimeRepositoryMock

fun StringArbitratorPreset() = StringArbitratorDefault(
    buildConfig = BuildConfigMock(),
    dateTimeFormatter = DateTimeFormatterDefault(DeviceCountryMock()),
    timeRepository = TimeRepositoryMock(),
)