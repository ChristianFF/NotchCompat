package ff.driven.self.notch

import android.os.Build
import android.text.TextUtils
import java.util.*

/**
 * Created by feifan on 2017/2/10.
 * Contacts me:404619986@qq.com
 */
object RomUtils {

    val isHuaweiRom: Boolean
        get() {
            val manufacturer = Build.MANUFACTURER
            return !manufacturer.isNullOrEmpty() && manufacturer.contains("HUAWEI")
        }

    val isMiuiRom: Boolean
        get() = !getSystemProperty("ro.miui.ui.version.name").isNullOrEmpty()

    val isOppoRom: Boolean
        get() {
            val a = getSystemProperty("ro.product.brand")
            return !a.isNullOrEmpty()
                && a!!.toLowerCase(Locale.getDefault()).contains("oppo")
        }

    val isVivoRom: Boolean
        get() {
            val a = getSystemProperty("ro.vivo.os.name")
            return !a.isNullOrEmpty()
                && a!!.toLowerCase(Locale.getDefault()).contains("funtouch")
        }

    val isMeizuRom: Boolean
        get() {
            val meizuFlymeOSFlag = getSystemProperty("ro.build.display.id")
            val meizuVersionFlag = getSystemProperty("ro.build.flyme.version")
            return !meizuVersionFlag.isNullOrEmpty()
                || meizuFlymeOSFlag?.toLowerCase(Locale.getDefault())?.contains("flyme") == true
        }

    val isSamsungRom: Boolean
        get() {
            val fingerPrint = Build.FINGERPRINT
            if (!TextUtils.isEmpty(fingerPrint)) {
                return fingerPrint.toLowerCase(Locale.getDefault()).contains("samsung")
            }
            val manufacturer = Build.MANUFACTURER
            return if (!TextUtils.isEmpty(manufacturer)) {
                manufacturer.toLowerCase(Locale.getDefault()).contains("samsung")
            } else {
                false
            }
        }

    private fun getSystemProperty(propName: String): String? {
        return SystemProperties[propName, null]
    }
}
