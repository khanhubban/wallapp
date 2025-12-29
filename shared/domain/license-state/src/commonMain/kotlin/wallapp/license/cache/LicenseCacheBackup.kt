package wallapp.license.cache

/**
 * Backups up license cache related information. Useful for data that can't be easily fetched in
 * the event of data destruction by the app. And example includes
 * [LicenseCache.featureMeterRemainingTime], which can't be easily fetched by re-polling the licensing
 * system.
 */
interface LicenseCacheBackup

