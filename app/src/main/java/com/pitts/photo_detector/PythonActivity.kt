package com.pitts.photo_detector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.chaquo.python.*
import com.chaquo.python.android.AndroidPlatform

class PythonActivity : AppCompatActivity() {

    private lateinit var __PYTHOH_INSTANCE: Python
    private lateinit var __STRING_STDIN: String
    private lateinit var __STRING_STDOUT: String

    private lateinit var __STDIN_VIEW: EditText
    private lateinit var __STDOUT_VIEW: TextView

    private lateinit var __PYTHONOBJ_SYS: PyObject
    private lateinit var __PYTHONOBJ_STDIO: PyObject
    private lateinit var __PYTHONOBJ_CONSOLE: PyObject
    private lateinit var __PYTHONOBJ_STDOUT: PyObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_python)
        __STDIN_VIEW = findViewById<EditText>(R.id.pyth_stdin)
        __STDOUT_VIEW = findViewById<TextView>(R.id.pyth_stdout)
        findViewById<ImageView>(R.id.pyth_back).setOnClickListener {
            finish()
        }
        findViewById<ImageButton>(R.id.pyth_send).setOnClickListener {
            __STRING_STDIN = __STDIN_VIEW.text.toString()
            sendCode()
        }
        findViewById<Button>(R.id.pyth_clearConsole).setOnClickListener {
            __STRING_STDOUT = ""
        }
        __STRING_STDOUT = String()
        if (!Python.isStarted()) Python.start(AndroidPlatform(this))
        __PYTHOH_INSTANCE = Python.getInstance()
        with(__PYTHOH_INSTANCE) {
            __PYTHONOBJ_SYS = getModule("sys")
            __PYTHONOBJ_STDIO = getModule("io")
            __PYTHONOBJ_CONSOLE = getModule("Interpreter")
            __PYTHONOBJ_STDOUT = __PYTHONOBJ_STDIO.callAttr("StringIO")
        }
        __PYTHONOBJ_SYS["stdout"] = __PYTHONOBJ_STDOUT
    }


    private fun sendCode() {
        try {
            __PYTHONOBJ_CONSOLE.callAttrThrows("mainTextCode", __STRING_STDIN)
            __STRING_STDOUT = __PYTHONOBJ_STDOUT.callAttr("getvalue").toString()
        } catch (__e: PyException) {
            __STRING_STDOUT = __STRING_STDOUT.plus(__e.message.toString())

        } catch (__t: Throwable) {
            __t.printStackTrace()
        }
        __STDOUT_VIEW.text = __STRING_STDOUT
        Log.d("__STRING_STDOUT", __STRING_STDOUT)
    }


}