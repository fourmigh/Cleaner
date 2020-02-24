package org.caojun.cleaner

import java.io.File

object CleanUtils {

    private var count = 0

    interface Listener {
        fun onFindFolder(folder: File)
        fun onDeleteEmptyFolder(folder: File)
        fun onDeleteEmptyFolderFailed(folder: File)
        fun onFinish()
    }

    fun cleanEmptyFolder(file: File, listener: Listener, first: Boolean = true) {
        if (first) {
            if (count > 0) {
                return
            }
            count = 0
        }
        count ++
        if (!file.exists()) {
            finish(listener)
            return
        }
        if (file.isDirectory) {
            listener.onFindFolder(file)
            val files = file.listFiles() ?: return
            if (files.isEmpty()) {
                if (file.delete()) {
                    listener.onDeleteEmptyFolder(file)
                } else {
                    listener.onDeleteEmptyFolderFailed(file)
                }
                finish(listener)
                return
            }
            for (f in files) {
                cleanEmptyFolder(f, listener, false)
            }
            val filesAfter = file.listFiles() ?: return
            if (filesAfter.isEmpty()) {
                if (file.delete()) {
                    listener.onDeleteEmptyFolder(file)
                } else {
                    listener.onDeleteEmptyFolderFailed(file)
                }
                finish(listener)
                return
            }
        }
        finish(listener)
    }

    private fun finish(listener: Listener) {
        count --
        if (count <= 0) {
            listener.onFinish()
        }
    }
}