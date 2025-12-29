package wallapp.tooling

import app.cash.licensee.LicenseeExtension
import app.cash.licensee.UnusedAction
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

fun Project.configureLicensee() {
    with(pluginManager) {
        apply("app.cash.licensee")
    }

    configure<LicenseeExtension> {
        allow("Apache-2.0")
        allow("MIT")
        allow("EPL-1.0")
        allow("BSD-2-Clause")
        allow("BSD-3-Clause")
        allowUrl("https://developer.android.com/studio/terms.html")
        allowUrl("https://developer.android.com/guide/playcore/license")
        allowUrl("https://opensource.org/licenses/MIT")
        allowUrl("https://github.com/haraldk/TwelveMonkeys#license")
        allowUrl("/LICENSE.txt")
        allowUrl("https://developer.android.com/google/play/integrity/overview#tos")
        allowUrl("https://spdx.org/licenses/MIT.txt")
        allowUrl("https://github.com/DanielMartinus/Konfetti/blob/main/LICENSE")
        allowUrl("https://github.com/mockito/mockito/blob/master/LICENSE")
        unusedAction(UnusedAction.IGNORE)
    }
}