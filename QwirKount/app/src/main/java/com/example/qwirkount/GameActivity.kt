package com.example.qwirkount

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class GameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
    }
}

//// Define the contract for the main activity
//class Contract : ActivityResultContract<String, OperationTime>(){
//    override fun createIntent(context: Context, input: String?): Intent =
//        Intent(context, ListOperations::class.java).putExtra("input", input)
//
//
//    override fun parseResult(resultCode: Int, intent: Intent?) = OperationTime(
//        operation = intent?.getStringExtra("operation"),
//        time = intent?.getStringExtra("time"),
//        index = intent?.getIntExtra("index", -1)
//    )