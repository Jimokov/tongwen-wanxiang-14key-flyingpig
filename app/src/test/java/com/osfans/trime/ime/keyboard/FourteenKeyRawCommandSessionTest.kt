/*
 * SPDX-FileCopyrightText: 2026 Rime community
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.osfans.trime.ime.keyboard

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class FourteenKeyRawCommandSessionTest :
    StringSpec({
        "a committed command restores its originating 14-key keyboard" {
            val session = FourteenKeyRawCommandSession()

            session.begin("wanxiang_14jian") shouldBe true
            session.complete() shouldBe "wanxiang_14jian"
            session.isActive shouldBe false
        }

        "cancel also restores the originating keyboard" {
            val session = FourteenKeyRawCommandSession()

            session.begin("wanxiang_14jian") shouldBe true
            session.cancel() shouldBe "wanxiang_14jian"
            session.isActive shouldBe false
        }

        "an active command layer cannot be entered a second time" {
            val session = FourteenKeyRawCommandSession()

            session.begin("wanxiang_14jian") shouldBe true
            session.begin("wanxiang_14jian") shouldBe false
        }
    })
