package wallapp.ads.inline

import kotlin.reflect.KClass

data class InlineAdInitDescriptor(
    val adDescriptorClass: KClass<*>,
    /*@LayoutRes*/ val rootLayoutId: Int,
)