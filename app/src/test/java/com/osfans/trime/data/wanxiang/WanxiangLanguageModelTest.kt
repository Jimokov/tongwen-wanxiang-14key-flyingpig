/*
 * SPDX-FileCopyrightText: 2026 Rime community
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.osfans.trime.data.wanxiang

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.io.ByteArrayInputStream
import java.nio.file.Files

class WanxiangLanguageModelTest :
    StringSpec({
        "a new grammar requires a successful deployment" {
            val root = Files.createTempDirectory("wanxiang-model").toFile()
            var deployed = ""
            var failed = ""
            val model = WanxiangLanguageModel(root, { deployed }, { deployed = it }, { failed }, { failed = it })

            model.install(ByteArrayInputStream(ByteArray(1024 * 1024) { 1 }))
            model.inspect()::class shouldBe WanxiangLanguageModel.Status.NeedsDeploy::class

            model.markDeploySuccess()
            model.inspect()::class shouldBe WanxiangLanguageModel.Status.Ready::class
            model.canActivateSchema("wanxiang_pro") shouldBe true
        }

        "a failed deployment does not make the imported grammar ready" {
            val root = Files.createTempDirectory("wanxiang-model").toFile()
            var deployed = ""
            var failed = ""
            val model = WanxiangLanguageModel(root, { deployed }, { deployed = it }, { failed }, { failed = it })

            model.install(ByteArrayInputStream(ByteArray(1024 * 1024) { 1 }))
            model.markDeployFailure()

            model.inspect()::class shouldBe WanxiangLanguageModel.Status.Failed::class
            model.canActivateSchema("wanxiang_pro") shouldBe false
        }

        "a rejected import keeps the last working grammar" {
            val root = Files.createTempDirectory("wanxiang-model").toFile()
            var deployed = ""
            var failed = ""
            val model = WanxiangLanguageModel(root, { deployed }, { deployed = it }, { failed }, { failed = it })
            model.install(ByteArrayInputStream(ByteArray(1024 * 1024) { 1 }))
            model.markDeploySuccess()

            runCatching { model.install(ByteArrayInputStream(ByteArray(32))) }.isFailure shouldBe true

            model.inspect()::class shouldBe WanxiangLanguageModel.Status.Ready::class
        }
    })
