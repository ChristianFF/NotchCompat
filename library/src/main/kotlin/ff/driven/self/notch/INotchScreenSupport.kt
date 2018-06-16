package ff.driven.self.notch

import android.graphics.Rect
import android.view.Window

/**
 * Created by feifan on 2018/6/15.
 * Contacts me:404619986@qq.com
 */
internal interface INotchScreenSupport {
    fun hasNotchInScreen(window: Window): Boolean

    fun getNotchSize(window: Window): List<Rect>

    fun setWindowLayoutAroundNotch(window: Window)

    fun setWindowLayoutBlockNotch(window: Window)
}
