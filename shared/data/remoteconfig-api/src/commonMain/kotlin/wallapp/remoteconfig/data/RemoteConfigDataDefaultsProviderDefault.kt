package wallapp.remoteconfig.data


object RemoteConfigDataDefaultsProviderDefault : RemoteConfigDataDefaultsProvider() {

    override val featureMeterInitialLevel: Double
        get() = 120.0
    override val featureMeterDepletionPerDay: Double
        get() = 28.57142857142857
    override val featureMeterRewardAdLevelIncrease: Double
        get() = 71.42857142857143
    override val featureMeterRewardAdLevelIncreaseSurplus: Double
        get() = 51.19047619047619
    override val featureMeterMaxSurplusLevel: Double
        get() = 400.0

    override val featureMeterWallpaperTierBase: Double
        get() = 0.0
    override val featureMeterAnimateWallpaper: Double
        get() = 14.285714285714285
    override val featureMeterEffectDim: Double
        get() = 28.57142857142857
    override val featureMeterGestures: Double
        get() = 57.14285714285714
    override val featureMeterWallpaperTierBronze: Double
        get() = 85.71428571428571
    override val featureMeterAutoSwitchWallpaperDarkTheme: Double
        get() = 114.28571428571428
    override val featureMeterWallpaperTierSilver: Double
        get() = 128.57142857142858
    override val featureMeterAutoSwitchWallpaperSchedule: Double
        get() = 142.85714285714286
    override val featureMeterWallpaperTierGold: Double
        get() = 171.42857142857142
    override val featureMeterWallpaperTierDiamond: Double
        get() = 200.0

}