/*
 * SPDX-FileCopyrightText: 2026 Rime community
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.osfans.trime.ime.keyboard

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class FourteenKeySelectionSessionTest :
    StringSpec({
        "selection returns to the originating keyboard after a commit" {
            val session = FourteenKeySelectionSession()

            session.begin("wanxiang_14jian", "zujoqdtu") shouldBe true
            session.complete() shouldBe "wanxiang_14jian"
            session.isActive shouldBe false
        }

        "cancel preserves the exact raw input from before lookup" {
            val session = FourteenKeySelectionSession()

            session.begin("wanxiang_14jian", "nihao") shouldBe true
            session.cancel() shouldBe FourteenKeySelectionSession.Snapshot("wanxiang_14jian", "nihao")
            session.isActive shouldBe false
        }

        "a second entry cannot replace an active selection snapshot" {
            val session = FourteenKeySelectionSession()

            session.begin("wanxiang_14jian", "nihao") shouldBe true
            session.begin("wanxiang_26jian", "hello") shouldBe false
            session.cancel() shouldBe FourteenKeySelectionSession.Snapshot("wanxiang_14jian", "nihao")
        }
    })
