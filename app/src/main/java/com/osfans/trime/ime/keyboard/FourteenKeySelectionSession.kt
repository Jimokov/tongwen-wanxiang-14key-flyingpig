/*
 * SPDX-FileCopyrightText: 2026 Rime community
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.osfans.trime.ime.keyboard

/**
 * Keeps the small amount of state that belongs to a 14-key "select character" trip.
 *
 * Rime owns the actual candidate filtering.  Trime only remembers which keyboard to
 * return to and the pre-lookup raw input needed to make Cancel non-destructive.
 */
class FourteenKeySelectionSession {
    data class Snapshot(
        val returnKeyboard: String,
        val rawInput: String,
    )

    private var snapshot: Snapshot? = null

    fun begin(
        returnKeyboard: String,
        rawInput: String,
    ): Boolean {
        if (returnKeyboard.isBlank() || rawInput.isBlank() || snapshot != null) return false
        snapshot = Snapshot(returnKeyboard, rawInput)
        return true
    }

    /** Ends a successful selection and returns the normal keyboard to restore. */
    fun complete(): String? = snapshot?.returnKeyboard.also { snapshot = null }

    /** Ends a cancelled selection and returns the exact pre-lookup input to restore. */
    fun cancel(): Snapshot? = snapshot.also { snapshot = null }

    fun clear() {
        snapshot = null
    }

    val isActive: Boolean
        get() = snapshot != null
}
