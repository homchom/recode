@file:JvmName("ServerConstants")

package io.github.homchom.recode.multiplayer

import org.intellij.lang.annotations.RegExp

const val SERVER_ADDRESS = "mcdiamondfire.com"

@RegExp const val USERNAME_PATTERN = """\w{3,16}"""
@RegExp const val PLOT_NAME_PATTERN = """.{1,128}"""
@RegExp const val BOOSTER_ARROW_PATTERN = """⏵⏵⏵?"""

const val DIAMOND_CHAR = '◆'
const val GREEN_ARROW_CHAR = '»'
const val RIGHT_ARROW_CHAR = '→'

const val TOKEN_NOTCH_CHAR = '□'

@RegExp const val LAGSLAYER_PATTERN = """\[LagSlayer]"""