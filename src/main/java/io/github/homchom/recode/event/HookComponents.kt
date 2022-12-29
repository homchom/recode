package io.github.homchom.recode.event

import io.github.homchom.recode.lifecycle.HookableModule
import io.github.homchom.recode.lifecycle.MutatesModuleState
import io.github.homchom.recode.lifecycle.RModule

/**
 * A [CustomHook] without a result.
 *
 * @see listenFrom
 */
interface UnitHook<C> : CustomHook<C, Unit> {
    fun run(context: C) = run(context, Unit)
}

/**
 * A [Hook] with a boolean result; this should be used for events whose hooks "validate" it and
 * determine whether the action that caused it should proceed.
 */
interface ValidatedHook<C> : Hook<C, Boolean>

/**
 * A [CustomHook] with children. When hooked onto by a [HookableModule], the children will
 * be implicitly added.
 */
class DependentHook<C, R : Any>(
    private val delegate: CustomHook<C, R>,
    vararg children: RModule
) : CustomHook<C, R> by delegate {
    private val children = children.clone()

    @MutatesModuleState
    override fun listenFrom(module: HookableModule, listener: HookListener<C, R>) {
        for (child in children) child.addParent(module)
        delegate.listenFrom(module, listener)
    }
}