/*
 * SPDX-FileCopyrightText: 2026 Rime community
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.osfans.trime.ime.keyboard

/**
 * Tracks the temporary full-keyboard layer used for Wanxiang slash commands.
 *
 * The command itself remains wholly inside Rime: this session only restores the
 * originating 14-key layout after the command is committed or cancelled.
 */
class FourteenKeyRawCommandSession {
    private var returnKeyboard: String? = null

    fun begin(keyboard: String): Boolean {
        if (keyboard.isBlank() || returnKeyboard != null) return false
        returnKeyboard = keyboard
        return true
    }

    fun complete(): String? = returnKeyboard.also { returnKeyboard = null }

    fun cancel(): String? = returnKeyboard.also { returnKeyboard = null }

    fun clear() {
        returnKeyboard = null
    }

    val isActive: Boolean
        get() = returnKeyboard != null
}
