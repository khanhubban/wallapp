package wallapp.settings

import com.russhwolf.settings.PropertiesSettings
import java.util.Properties

/**
 * A [Settings] implementation that uses a [Properties] instance as its underlying storage.
 *
 * By default, [properties] will only save to memory.
 */
fun SettingsMultiplatformProperties(properties: Properties = Properties()): PropertiesSettings =
    PropertiesSettings(properties)