/*
 * SPDX-FileCopyrightText: 2026 Rime community
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.osfans.trime.ime.keyboard

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class WanxiangKeyboardLayoutTest :
    StringSpec({
        "stored full layout resolves to the shared 26-key keyboard" {
            WanxiangKeyboardLayout.fromStoredValue("full") shouldBe WanxiangKeyboardLayout.Full
            WanxiangKeyboardLayout.Full.keyboardId shouldBe "default"
        }

        "stored fourteen layout resolves independently of the schema" {
            WanxiangKeyboardLayout.fromStoredValue("fourteen") shouldBe WanxiangKeyboardLayout.Fourteen
            WanxiangKeyboardLayout.Fourteen.keyboardId shouldBe "wanxiang_14jian"
        }

        "unknown persisted values fall back safely to the full layout" {
            WanxiangKeyboardLayout.fromStoredValue("legacy-t9") shouldBe WanxiangKeyboardLayout.Full
        }
    })
