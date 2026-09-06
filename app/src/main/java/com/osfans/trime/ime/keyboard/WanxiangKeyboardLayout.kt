/*
 * SPDX-FileCopyrightText: 2026 Rime community
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.osfans.trime.ime.keyboard

/** A persisted layout choice that is intentionally independent of the active schema. */
enum class WanxiangKeyboardLayout(
    val storedValue: String,
    val keyboardId: String,
) {
    Full("full", "default"),
    Fourteen("fourteen", "wanxiang_14jian"),
    ;

    companion object {
        fun fromStoredValue(value: String): WanxiangKeyboardLayout =
            entries.firstOrNull { it.storedValue == value } ?: Full
    }
}
