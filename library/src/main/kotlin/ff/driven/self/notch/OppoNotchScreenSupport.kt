package ff.driven.self.notch

import android.graphics.Rect
import android.support.annotation.RequiresApi
import android.view.View
import android.view.Window
import android.view.WindowManager
import java.util.*

/**
 * Created by feifan on 2018/5/15.
 * Contacts me:404619986@qq.com
 */
internal class OppoNotchScreenSupport : INotchScreenSupport {

    @RequiresApi(api = 26)
    override fun hasNotchInScreen(window: Window): Boolean {
        return try {
            window.context.packageManager
                .hasSystemFeature("com.oppo.feature.screen.heteromorphism")
        } catch (ignored: Exception) {
            false
        }
    }

    //目前Oppo刘海屏机型尺寸规格都是统一的,显示屏宽度为1080px，高度为2280px,刘海区域宽度为324px, 高度为80px
    @RequiresApi(api = 26)
    override fun getNotchSize(window: Window): List<Rect> {
        val result = ArrayList<Rect>()
        val context = window.context
        val rect = Rect()
        val displayMetrics = context.resources.displayMetrics
        val notchWidth = 324
        val notchHeight = 80
        rect.left = (displayMetrics.widthPixels - notchWidth) / 2
        rect.right = rect.left + notchWidth
        rect.top = 0
        rect.bottom = notchHeight
        result.add(rect)
        return result
    }

    @RequiresApi(api = 26)
    override fun setWindowLayoutAroundNotch(window: Window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        var systemUiVisibility = window.decorView.systemUiVisibility
        systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.decorView.systemUiVisibility = systemUiVisibility
    }

    @RequiresApi(api = 26)
    override fun setWindowLayoutBlockNotch(window: Window) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        var systemUiVisibility = window.decorView.systemUiVisibility
        systemUiVisibility = systemUiVisibility and View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN.inv()
        systemUiVisibility = systemUiVisibility and View.SYSTEM_UI_FLAG_LAYOUT_STABLE.inv()
        window.decorView.systemUiVisibility = systemUiVisibility
    }
}
