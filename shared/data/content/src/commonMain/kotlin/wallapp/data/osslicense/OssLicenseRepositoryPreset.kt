package wallapp.data.osslicense

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

object OssLicenseRepositoryPreset : OssLicenseRepository {

    override val ossLicenses: Flow<List<OssLicense>> = flowOf(OssLicenses.CurrentPlatform)
}