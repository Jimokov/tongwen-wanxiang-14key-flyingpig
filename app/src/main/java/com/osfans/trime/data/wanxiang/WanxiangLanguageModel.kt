/*
 * SPDX-FileCopyrightText: 2026 Rime community
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.osfans.trime.data.wanxiang

import com.osfans.trime.data.base.DataManager
import com.osfans.trime.data.prefs.AppPrefs
import java.io.File
import java.io.InputStream
import java.security.MessageDigest

/**
 * Owns the external Wanxiang grammar file.  It deliberately lives outside themes and APK assets:
 * a package may reference the model but must never contain it.
 */
class WanxiangLanguageModel(
    private val userDataDir: File = DataManager.userDataDir,
    private val deployedFingerprint: () -> String = {
        AppPrefs.defaultInstance().profile.wanxiangModelDeployedFingerprint.getValue()
    },
    private val saveDeployedFingerprint: (String) -> Unit = {
        AppPrefs.defaultInstance().profile.wanxiangModelDeployedFingerprint.setValue(it)
    },
    private val failedFingerprint: () -> String = {
        AppPrefs.defaultInstance().profile.wanxiangModelFailedFingerprint.getValue()
    },
    private val saveFailedFingerprint: (String) -> Unit = {
        AppPrefs.defaultInstance().profile.wanxiangModelFailedFingerprint.setValue(it)
    },
) {
    data class FileInfo(val bytes: Long, val fingerprint: String)

    sealed interface Status {
        data object Missing : Status
        data class NeedsDeploy(val file: FileInfo) : Status
        data class Ready(val file: FileInfo) : Status
        data class Failed(val file: FileInfo) : Status
    }

    val file: File get() = File(userDataDir, FILE_NAME)

    fun inspect(): Status {
        val info = fileInfo() ?: return Status.Missing
        return when (info.fingerprint) {
            deployedFingerprint() -> Status.Ready(info)
            failedFingerprint() -> Status.Failed(info)
            else -> Status.NeedsDeploy(info)
        }
    }

    /**
     * Copy to a sibling first and only replace the active grammar after the complete copy passes
     * its size check. An interrupted document-provider read therefore leaves the old model intact.
     */
    fun install(input: InputStream): FileInfo {
        userDataDir.mkdirs()
        val staged = File(userDataDir, ".$FILE_NAME.importing")
        staged.delete()
        try {
            input.use { source -> staged.outputStream().use(source::copyTo) }
            val info = fileInfo(staged) ?: error("Wanxiang grammar import is empty")
            require(info.bytes >= MINIMUM_BYTES) { "Wanxiang grammar import is too small" }
            replaceAtomically(staged, file)
            saveDeployedFingerprint("")
            saveFailedFingerprint("")
            return info
        } finally {
            staged.delete()
        }
    }

    fun markDeploySuccess() {
        val info = fileInfo() ?: return
        saveDeployedFingerprint(info.fingerprint)
        saveFailedFingerprint("")
    }

    fun markDeployFailure() {
        fileInfo()?.let { saveFailedFingerprint(it.fingerprint) }
    }

    fun canActivateSchema(schemaId: String): Boolean =
        !schemaId.startsWith("wanxiang", ignoreCase = true) || inspect() is Status.Ready

    private fun fileInfo(target: File = file): FileInfo? {
        if (!target.isFile || target.length() < MINIMUM_BYTES) return null
        return FileInfo(target.length(), target.inputStream().use(::sha256))
    }

    private fun sha256(input: InputStream): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        while (true) {
            val count = input.read(buffer)
            if (count < 0) break
            digest.update(buffer, 0, count)
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    private fun replaceAtomically(staged: File, target: File) {
        val backup = File(target.parentFile, ".${target.name}.backup")
        backup.delete()
        if (target.exists() && !target.renameTo(backup)) error("Cannot back up existing Wanxiang grammar")
        try {
            if (!staged.renameTo(target)) error("Cannot activate imported Wanxiang grammar")
            backup.delete()
        } catch (e: Exception) {
            if (backup.exists()) backup.renameTo(target)
            throw e
        }
    }

    companion object {
        const val FILE_NAME = "wanxiang-lts-zh-hans.gram"
        // A small floor rejects placeholder files while permitting future official model revisions.
        private const val MINIMUM_BYTES = 1L * 1024 * 1024
    }
}
