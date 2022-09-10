package io.github.homchom.recode.server

import org.intellij.lang.annotations.RegExp

@RegExp const val USERNAME_PATTERN = """\w{3,16}?"""
@RegExp const val PLOT_NAME_PATTERN = """.{1,128}"""