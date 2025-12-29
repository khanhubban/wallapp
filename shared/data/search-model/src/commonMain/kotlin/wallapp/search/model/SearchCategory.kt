package wallapp.search.model

sealed interface SearchCategory {

    val key: String

    data object ThreeD : SearchCategory {
        override val key: String
            get() = "3d"
    }

//    data object Landscape : SearchCategory {
//        override val key: String
//            get() = "landscape"
//    }

    data object Abstract : SearchCategory {
        override val key: String
            get() = "abstract"
    }

    data object Photography : SearchCategory {
        override val key: String
            get() = "photography"
    }

    data object Illustration : SearchCategory {
        override val key: String
            get() = "illustration"
    }

//    data object Vector : SearchCategory {
//        override val key: String
//            get() = "vector"
//    }

//    data object Amoled : SearchCategory {
//        override val key: String
//            get() = "amoled"
//    }

//    data object Cityscapes : SearchCategory {
//        override val key: String
//            get() = "cityscapes"
//    }

//    data object Digital : SearchCategory {
//        override val key: String
//            get() = "digital"
//    }

    data object Pattern : SearchCategory {
        override val key: String
            get() = "pattern"
    }

//    data object Surreal : SearchCategory {
//        override val key: String
//            get() = "surreal"
//    }

    companion object {
        val All by lazy {
            listOf(
                ThreeD,
                Abstract,
//                Amoled,
//                Cityscapes,
//                Digital,
//                Landscape,
                Illustration,
                Photography,
                Pattern,
//                Surreal,
//                Vector,
            )
        }

        fun fromKey(key: String): SearchCategory? {
            return All.firstOrNull { it.key == key }
        }
    }
}