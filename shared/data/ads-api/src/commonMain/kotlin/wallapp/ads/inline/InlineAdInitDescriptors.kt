package wallapp.ads.inline

import kotlin.reflect.KClass


abstract class InlineAdInitDescriptors {

    abstract val adInitDescriptors: List<InlineAdInitDescriptor>

    fun getByInlineAdDescriptor(adDescriptor: InlineAdDescriptor): InlineAdInitDescriptor {
        return adInitDescriptors.find {
            it.adDescriptorClass == adDescriptor::class
        }.let {
            requireNotNull(it) {
                "Unable to find adDescriptor: ${adDescriptor::class.simpleName} in list: [${adInitDescriptors.joinToString() { it.adDescriptorClass.simpleName ?: "" }}]"
            }
            it
        }
    }

    fun containsDescriptorClass(clazz: KClass<out InlineAdDescriptor>): Boolean {
        return adInitDescriptors.find { it.adDescriptorClass == clazz } != null
    }
}