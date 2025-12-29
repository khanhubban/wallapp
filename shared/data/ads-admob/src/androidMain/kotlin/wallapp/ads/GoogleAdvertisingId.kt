package wallapp.ads

import android.content.Context
import com.google.android.gms.ads.identifier.AdvertisingIdClient.getAdvertisingIdInfo
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import java.io.IOException

object GoogleAdvertisingId {

    fun getAdvertisingId(context: Context): String? {
        return try {
            getAdvertisingIdInfo(context).id
        } catch (e: IOException) {
            null
        } catch (e: IllegalStateException) {
            null
        } catch (e: GooglePlayServicesNotAvailableException) {
            null
        } catch (e: GooglePlayServicesRepairableException) {
            null
        }
    }

}