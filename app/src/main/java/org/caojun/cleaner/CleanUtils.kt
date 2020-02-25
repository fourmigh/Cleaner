package org.caojun.cleaner

import android.content.Context
import org.jetbrains.anko.runOnUiThread
import java.io.File

object CleanUtils {

    private var count = 0

    interface Listener {
        fun onFindFolder(folder: File)
        fun onDeleteFile(file: File)
        fun onFinish()
    }

    fun cleanFolderOrFile(context: Context, file: File, listener: Listener, first: Boolean = true) {
        if (first) {
            if (count > 0) {
                return
            }
            count = 0
        }
        count ++
        if (!file.exists()) {
            finish(context, listener)
            return
        }
        if (file.isDirectory) {
            context.runOnUiThread {
                listener.onFindFolder(file)
            }
            val files = file.listFiles() ?: return
            if (files.isEmpty()) {
                if (file.delete()) {
                    context.runOnUiThread {
                        listener.onDeleteFile(file)
                    }
                }
                finish(context, listener)
                return
            }
            for (f in files) {
                cleanFolderOrFile(context, f, listener, false)
            }
            val filesAfter = file.listFiles() ?: return
            if (filesAfter.isEmpty()) {
                if (file.delete()) {
                    context.runOnUiThread {
                        listener.onDeleteFile(file)
                    }
                }
                finish(context, listener)
                return
            }
        } else if (file.isFile) {
            if (file.name.endsWith(".log") || file.length() == 0L) {
                if (file.delete()) {
                    context.runOnUiThread {
                        listener.onDeleteFile(file)
                    }
                }
            }
        }
        finish(context, listener)
    }

    private fun finish(context: Context, listener: Listener) {
        count --
        if (count <= 0) {
            context.runOnUiThread {
                listener.onFinish()
            }
        }
    }
}