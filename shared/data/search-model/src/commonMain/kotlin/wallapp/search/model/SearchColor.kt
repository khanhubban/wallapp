package wallapp.search.model

import wallapp.graphics.Color

sealed interface SearchColor {

    val key: String
    val color: Color

//    data object Purple : SearchColor {
//        override val key: String
//            get() = "purple"
//        override val color: Color
//            get() = Color(0xff8e278b)
//    }

    data object Pink : SearchColor {
        override val key: String
            get() = "pink"
        override val color: Color
            get() = Color(0xffef5fa7)
    }

    data object Blue : SearchColor {
        override val key: String
            get() = "blue"
        override val color: Color
            get() = Color(0xff01a0fe)
    }

    data object Green : SearchColor {
        override val key: String
            get() = "green"
        override val color: Color
            get() = Color(0xff1fb100)
    }

    data object Yellow : SearchColor {
        override val key: String
            get() = "yellow"
        override val color: Color
            get() = Color(0xfff8ba00)
    }

//    data object Orange : SearchColor {
//        override val key: String
//            get() = "orange"
//        override val color: Color
//            get() = Color(0xfff8ba00)
//    }

    data object Red : SearchColor {
        override val key: String
            get() = "red"
        override val color: Color
            get() = Color(0xffed220d)
    }

    data object Dark : SearchColor {
        override val key: String
            get() = "dark"
        override val color: Color
            get() = Color(0xff4f4f4f)
    }

    data object Light : SearchColor {
        override val key: String
            get() = "light"
        override val color: Color
            get() = Color(0xfff2f1f6)
    }

    companion object {
        val All by lazy {
            listOf(
                Blue,
                Green,
                Yellow,
                Red,
                Pink,
                Light,
                Dark,
            )
        }

        fun fromKey(key: String): SearchColor? {
            val keyLowercase = key.lowercase()
            return All.firstOrNull { it.key == keyLowercase }
        }
    }
}