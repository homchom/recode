package io.github.homchom.recode.render.text

import io.github.homchom.recode.ui.text.VanillaStyle
import net.kyori.adventure.text.Component
import net.minecraft.util.FormattedCharSequence

/**
 * Builds a composite [FormattedCharSequence] by applying [builder].
 *
 * Use [io.github.homchom.recode.ui.text] for higher-level [Component] creation, which supports more than literals.
 *
 * @see FormattedCharSequenceBuilder
 */
inline fun formattedCharSequence(builder: FormattedCharSequenceBuilder.() -> Unit) =
    FormattedCharSequenceBuilder().apply(builder).build()

/**
 * @see forward
 * @see backward
 *
 * @see formattedCharSequence
 */
@JvmInline
value class FormattedCharSequenceBuilder private constructor(private val list: MutableList<FormattedCharSequence>) {
    constructor() : this(mutableListOf())

    fun build(): FormattedCharSequence = FormattedCharSequence.composite(list)

    /**
     * Appends [string] in forward order with [style].
     */
    fun forward(string: String, style: VanillaStyle = VanillaStyle.EMPTY) {
        list += FormattedCharSequence.forward(string, style)
    }

    /**
     * Appends [string] in backward order with [style].
     */
    fun backward(string: String, style: VanillaStyle = VanillaStyle.EMPTY) {
        list += FormattedCharSequence.backward(string, style)
    }

    /**
     * Appends the character with code point [code], with [style].
     */
    fun codepoint(code: Int, style: VanillaStyle = VanillaStyle.EMPTY) {
        list += FormattedCharSequence.codepoint(code, style)
    }

    /**
     * Appends a pre-existing [formattedCharSequence].
     */
    fun append(formattedCharSequence: FormattedCharSequence) {
        list += formattedCharSequence
    }
}