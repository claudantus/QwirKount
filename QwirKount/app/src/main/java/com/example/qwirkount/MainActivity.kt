package com.example.qwirkount

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.Toast
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.*
import java.io.Serializable


class MainActivity : AppCompatActivity() {
    // list of available players
    private val mPlayerList = mutableListOf<String>()

    // list of selected players
    private val mPlayerSelectedList = mutableListOf<String>()

    // test which is changed from dialog
    private var mText: String = ""

    // max number of players in mPlayerList
    private val mMaxPlayerList: Int = 4
    // max number of players in the selection (maybe rename?)
    private val mMaxPlayerGame: Int = 4
    // min number of players in selection in order to start a game
    private val mMinPlayers: Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        mPlayerList.add("Blarius")
//        mPlayerList.add("Idiota")
//        mPlayerList.add("Bert")
//
        mPlayerSelectedList.add("p1")
        mPlayerSelectedList.add("p2")
        mPlayerSelectedList.add("p3")
        mPlayerSelectedList.add("p4")

        // adapter for the player list
        val mPlayerListAdapter = ArrayAdapter(this,
            R.layout.listview_item, mPlayerList)

        playerListView.adapter = mPlayerListAdapter

        //adapter for the selected player list
        val mPlayerSelectedAdapter = ArrayAdapter(this,
            R.layout.listview_item, mPlayerSelectedList)

        playersSelectedListView.adapter = mPlayerSelectedAdapter

        // dialog when clicking on players list item
        playerListView.setOnItemClickListener { _, _, position, _ ->
            clickPlayerDialog(mPlayerListAdapter, mPlayerSelectedAdapter, position)
        }

        // dialog when clicking on selected player item
        playersSelectedListView.setOnItemClickListener { _, _, position, _ ->
            clickSelectedPlayerDialog(mPlayerSelectedAdapter, position)
        }

        // dialog when adding a new player
        playerNewButton.setOnClickListener{
            if (mPlayerList.size>=mMaxPlayerList){
                Toast.makeText(this, "Player list full", Toast.LENGTH_SHORT).show()
            }
            else{
                newPlayerDialog(mPlayerListAdapter)
            }
        }

        // contract for the game activity
        val gameContract = registerForActivityResult(GameActivity.Contract()){result->
            val winner = result.winner!!
            Toast.makeText(this, "The winner is:", Toast.LENGTH_SHORT)
        }

        // start the game on button click
        startGameButton.setOnClickListener {
            val numPlayers = mPlayerSelectedList.size
            val showTotal = showTotalCheckBox.isChecked
            if (numPlayers >= mMinPlayers){

                val playerList: ArrayList<String> = ArrayList(mPlayerSelectedList)
                var game = GameSetup(showTotal, numPlayers, playerList, 0, "")
                gameContract.launch(game)
            }
            else {
                Toast.makeText(this, "Not enough players. Need at least 2.", Toast.LENGTH_SHORT).show()
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

            // check the input for validity: Not empty! (or only whitespace)
            if (mText.none { !it.isWhitespace() }){
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

        // delete player from list
        builder.setPositiveButton("Delete", DialogInterface.OnClickListener { _, _ ->
            mPlayerList.removeAt(position)
            adapterList.notifyDataSetChanged()
        })

        // add player to selection
        builder.setNeutralButton("Add", DialogInterface.OnClickListener { _, _ ->
            // Here you get get input text from the Edittext
            if (mPlayerSelectedList.size < mMaxPlayerGame){
                mText = mPlayerList[position]!!
                mPlayerSelectedList.add(mText)
                adapterSelected.notifyDataSetChanged()
            }
            else{
                Toast.makeText(this, "Game is full", Toast.LENGTH_SHORT).show()
            }
        })

        // do nothing
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() })

        builder.show()
    }

    private fun clickSelectedPlayerDialog(adapterSelected: ArrayAdapter<String?>, position: Int){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)

        builder.setTitle("Player: " + mPlayerSelectedList[position])

        // remove player from selection
        builder.setNeutralButton("Remove", DialogInterface.OnClickListener { _, _ ->
            mPlayerSelectedList.removeAt(position)
            adapterSelected.notifyDataSetChanged()
        })

        // do nothing
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        builder.show()
    }

}

// game setup class (input to the game activity contract. as a result, the winner will be added
class GameSetup(val showTotal: Boolean?, val numPlayers: Int?, val players: ArrayList<String>, val time: Int?, val winner: String?):Serializable

