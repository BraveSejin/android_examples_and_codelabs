package com.sejin.activityresultapiexample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btn = findViewById<Button>(R.id.button)

        val launcher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result -> // result: ActivityResult
            if (result.resultCode == RESULT_OK) {
                val str = result.data?.getStringExtra(STRING_INTENT_KEY)
                Toast.makeText(this@MainActivity, str, Toast.LENGTH_LONG).show()
            }
        }
        val intent = Intent(this, SecondActivity::class.java)
        btn.setOnClickListener { launcher.launch(intent) }
    }

    companion object {
        const val STRING_INTENT_KEY = "mykey"
    }
}