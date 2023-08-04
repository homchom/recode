@file:JvmName("ServerConstants")

package io.github.homchom.recode.multiplayer

import org.intellij.lang.annotations.RegExp

const val SERVER_ADDRESS = "mcdiamondfire.com"

@RegExp const val USERNAME_PATTERN = """\w{3,16}"""
@RegExp const val PLOT_NAME_PATTERN = """.{1,128}"""
@RegExp const val BOOSTER_ARROW_PATTERN = """⏵{2,3}"""

const val DIAMOND = '◆'
const val MAIN_ARROW = '»'
const val RIGHT_ARROW = '→'
const val SUPPORT_ARROW = '▶'
const val BOOSTER_ARROW = '⏵'

const val TOKEN_NOTCH_CHAR = '□'

const val LAGSLAYER_PREFIX = """[LagSlayer]"""