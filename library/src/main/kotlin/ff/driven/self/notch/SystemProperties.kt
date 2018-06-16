package ff.driven.self.notch

import java.lang.reflect.Method

object SystemProperties {
    private val getStringProperty = getMethod(getClass("android.os.SystemProperties"))

    private fun getClass(name: String): Class<*>? {
        return try {
            Class.forName(name) ?: throw ClassNotFoundException()
        } catch (e: ClassNotFoundException) {
            try {
                ClassLoader.getSystemClassLoader().loadClass(name)
            } catch (e1: ClassNotFoundException) {
                null
            }
        }
    }

    private fun getMethod(clz: Class<*>?): Method? {
        return try {
            clz?.getMethod("get", String::class.java)
        } catch (e: Exception) {
            null
        }
    }

    operator fun get(key: String): String {
        return try {
            val value = getStringProperty?.invoke(null, key) as String?
            value?.trim() ?: ""
        } catch (ignored: Exception) {
            ""
        }
    }

    operator fun get(key: String, def: String?): String? {
        return try {
            val value = getStringProperty?.invoke(null, key) as String?
            if (value?.trim().isNullOrEmpty()) {
                def
            } else {
                value!!.trim()
            }
        } catch (ignored: Exception) {
            def
        }
    }
}
