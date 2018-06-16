package ff.driven.self.notch

import android.os.Build
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

    private fun getSystemProperty(propName: String): String? {
        return SystemProperties[propName, null]
    }
}
