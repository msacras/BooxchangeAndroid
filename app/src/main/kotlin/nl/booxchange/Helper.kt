package nl.booxchange

/**
 * Created by usr1 on 3/19/18.
 */

class Helper private constructor() {
    companion object {
        fun removePlus(code: String?): String? {
            if (code == null) {
                return null
            }
            return if (code.trim { it <= ' ' }.startsWith("+")) code.substring(1) else code
        }

        fun addPlus(code: String?): String? {
            if (code == null) {
                return null
            }
            return "+" + code
        }
    }
}