package org.caojun.cleaner

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.doAsync
import java.io.File

class MainActivity : AppCompatActivity() {

    private val alSuccess = ArrayList<String>()
    private val alFail = ArrayList<String>()
    private var aaSuccess: ArrayAdapter<String>? = null
    private var aaFail: ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
            doAsync {
                alSuccess.clear()
                alFail.clear()
                doCleanFolders()
            }
        }

        val path = "${Environment.getExternalStorageDirectory().absolutePath}/CamScanner"
        val folder = File(path)
        Log.d("folder.isDirectory", "${folder.isDirectory}")
        if (folder.isDirectory) {
            val list = folder.listFiles()
            for (i in list.indices) {
                Log.d("file: $i", list[i].absolutePath)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun doCleanFolders() {
        val root = Environment.getExternalStorageDirectory()
        CleanUtils.cleanEmptyFolder(root, object : CleanUtils.Listener {

            override fun onFinish() {
                runOnUiThread {
                    tvInfo.text = "Finished"
                }
            }

            override fun onFindFolder(folder: File) {
                runOnUiThread {
                    tvInfo.text = folder.absolutePath
                }
            }

            override fun onDeleteEmptyFolder(folder: File) {
                runOnUiThread {
                    addSuccess(folder)
                }
            }

            override fun onDeleteEmptyFolderFailed(folder: File) {
                runOnUiThread {
                    addFail(folder)
                }
            }
        })
    }

    private fun addSuccess(file: File) {
        alSuccess.add(file.absolutePath)
        if (aaSuccess == null) {
            aaSuccess = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1, alSuccess
            )
            lvSuccess.adapter = aaSuccess
        } else {
            aaSuccess?.notifyDataSetChanged()
            if (aaSuccess != null) {
                lvSuccess.smoothScrollToPosition(aaSuccess!!.count - 1)
            }
        }
    }

    private fun addFail(file: File) {
        alFail.add(file.absolutePath)
        if (aaFail == null) {
            aaFail = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1, alFail
            )
            lvSuccess.adapter = aaSuccess
        } else {
            aaFail?.notifyDataSetChanged()
            if (aaFail != null) {
                lvFail.smoothScrollToPosition(aaFail!!.count - 1)
            }
        }
    }
}
