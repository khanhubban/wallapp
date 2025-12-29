package wallapp.data.osslicense

import kotlinx.coroutines.flow.Flow

interface OssLicenseRepository {

    val ossLicenses: Flow<List<OssLicense>>
}