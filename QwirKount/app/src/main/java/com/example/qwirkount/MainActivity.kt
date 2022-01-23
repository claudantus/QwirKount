package com.example.qwirkount

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.Toast
import android.view.View
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val mPlayerList = mutableListOf<String?>()
    private val mPlayerSelectedList = mutableListOf<String?>()


    private var mText = ""
    private val maxPlayerList = 4
    private val maxPlayerGame = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mPlayerList.add("Blarius")
        mPlayerList.add("Idiota")
        mPlayerList.add("Bert")




        val mPlayerListAdapter = ArrayAdapter(this,
            R.layout.listview_item, mPlayerList)

        playerListView.adapter = mPlayerListAdapter


        val mPlayerSelectedAdapter = ArrayAdapter(this,
            R.layout.listview_item, mPlayerSelectedList)

        playersSelectedListView.adapter = mPlayerSelectedAdapter


        playerListView.setOnItemClickListener { _, view, position, _ ->
            clickPlayerDialog(mPlayerListAdapter, mPlayerSelectedAdapter, position)


        }

        playersSelectedListView.setOnItemClickListener { _, view, position, _ ->
            clickSelectedPlayerDialog(mPlayerListAdapter, mPlayerSelectedAdapter, position)
        }

        playerNewButton.setOnClickListener{
            if (mPlayerList.size>=maxPlayerList){
                Toast.makeText(this, "Player list full", Toast.LENGTH_SHORT).show()
            }
            else{
                newPlayerDialog(mPlayerListAdapter)
            }
        }




    }

    private fun newPlayerDialog(adapter: ArrayAdapter<String?>){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Enter Player Name")
        // Set up the input
        val input = EditText(this)
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.hint = "New Player"
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
            // Here you get get input text from the Edittext
            mText = input.text.toString()

            if (mText.filter{!it.isWhitespace()}.isEmpty()){
                Toast.makeText(this, "Invalid Name!", Toast.LENGTH_SHORT).show()
            }
            else{
                mPlayerList.add(mText)
                Toast.makeText(this, mPlayerList.joinToString(), Toast.LENGTH_LONG).show()

                adapter.notifyDataSetChanged()
            }


        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        builder.show()
    }

    private fun clickPlayerDialog(adapterList: ArrayAdapter<String?>, adapterSelected: ArrayAdapter<String?>, position: Int){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Player: " + mPlayerList[position])
        // Set up the input

        builder.setPositiveButton("Delete", DialogInterface.OnClickListener { _, _ ->
            mPlayerList.removeAt(position)
            adapterList.notifyDataSetChanged()
        })

        // Set up the buttons
        builder.setNeutralButton("Add", DialogInterface.OnClickListener { _, _ ->
            // Here you get get input text from the Edittext
            if (mPlayerSelectedList.size < maxPlayerGame){
                mText = mPlayerList[position]!!
                mPlayerSelectedList.add(mText)
                adapterSelected.notifyDataSetChanged()
            }
            else{
                Toast.makeText(this, "Game is full", Toast.LENGTH_SHORT).show()
            }

        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        builder.show()
    }

    private fun clickSelectedPlayerDialog(adapterList: ArrayAdapter<String?>, adapterSelected: ArrayAdapter<String?>, position: Int){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Player: " + mPlayerList[position])
        // Set up the input

        builder.setNeutralButton("Remove", DialogInterface.OnClickListener { _, _ ->
            mPlayerSelectedList.removeAt(position)
            adapterSelected.notifyDataSetChanged()
        })

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        builder.show()
    }

}
//

//    // Custom contract defined in ListOperations class
//    val operationsContract = registerForActivityResult(ListOperations.Contract()){result->
//        if (result.time!!.toInt() > -1) {
//            if(result.index == -1){
//                mOperationslist.add(result.operation + " : " + result.time + " s")
//                mOperationsadapter.notifyDataSetChanged()
//                Toast.makeText(this, "Is good!", Toast.LENGTH_SHORT).show()
//            }
//            else{
//                mOperationslist[result.index!!] =  result.operation + " : " + result.time + " s"
//                mOperationsadapter.notifyDataSetChanged()
//                Toast.makeText(this, "Change is good!", Toast.LENGTH_SHORT).show()
//            }
//        }
//        else if (result.time!!.toInt() == -1){
//            Toast.makeText(this, "Input not valid!", Toast.LENGTH_SHORT).show()
//        }
//        else if (result.time!!.toInt() == -2){
//            mOperationslist.removeAt(result.index!!)
//            mOperationsadapter.notifyDataSetChanged()
//            Toast.makeText(this, "Deleted!", Toast.LENGTH_SHORT).show()
//        }
//        else if (result.time!!.toInt() == -3){
//            Toast.makeText(this, "Canceled!", Toast.LENGTH_SHORT).show()
//        }
//    }