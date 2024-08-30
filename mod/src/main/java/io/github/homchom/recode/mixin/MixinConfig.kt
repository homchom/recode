package io.github.homchom.recode.mixin

import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo
import org.spongepowered.asm.service.MixinService

// recode's mixin configuration plugin, referenced in resources/recode.mixins.json
class MixinPluginRecode : IMixinConfigPlugin {
    override fun shouldApplyMixin(targetClassName: String, mixinClassName: String): Boolean {
        // handle optional mixins
        val annotations = MixinService.getService()
            .bytecodeProvider
            .getClassNode(mixinClassName)
            .visibleAnnotations
            ?: return true
        annotations.filterIsInstance<MixinConditional>()
            .singleOrNull()
            ?.run { return shouldApplyMixin }

        return true
    }

    override fun onLoad(mixinPackage: String) {}
    override fun getRefMapperConfig() = null
    override fun acceptTargets(myTargets: MutableSet<String>, otherTargets: MutableSet<String>) {}
    override fun getMixins() = null

    override fun preApply(
        targetClassName: String,
        targetClass: ClassNode,
        mixinClassName: String,
        mixinInfo: IMixinInfo
    ) {}

    override fun postApply(
        targetClassName: String,
        targetClass: ClassNode,
        mixinClassName: String,
        mixinInfo: IMixinInfo
    ) {}
}