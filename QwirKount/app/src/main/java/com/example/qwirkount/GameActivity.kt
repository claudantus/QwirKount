package com.example.qwirkount

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContract
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {
    // players from the selection in main activity
    private lateinit var mPlayers: MutableList<String>
    // sorted list of players (will be filled when first player is selected)
    private lateinit var mPlayersSorted: MutableList<String>
    private val mScoresList = ArrayList<Model>()

    // number of players in the game (2-4)
    private var mNumPlayers: Int = 0
    // current round
    private var mRound: Int = 0
    // round time
    private var mTime: Int = 0
    // current game time
    private var mGameTime: Double = 0.0
    // index of the player who starts
    private var mFirstPlayerIndex: Int = 0
    // name of the starting player
    private var mFirstPlayerName: String = ""
    // name of the winner
    private var mWinner: String = ""

    private var mShowTotals: Boolean = true

    override fun onBackPressed() {
        val resultIntent = Intent()
        resultIntent.putExtra("time", mTime)
        resultIntent.putExtra("players", ArrayList(mPlayers))
        resultIntent.putExtra("numPlayers", mNumPlayers)
        resultIntent.putExtra("winner", mWinner)
        setResult(Activity.RESULT_CANCELED, resultIntent)
        finish()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // get the input from the main activity
        mPlayers = intent.getStringArrayListExtra("players")!!
        mPlayersSorted = MutableList(mPlayers.size){""}
        mTime = intent.getIntExtra("time", 0)
        mNumPlayers = intent.getIntExtra("numPlayers", 2)
        mShowTotals = intent.getBooleanExtra("totals", true)

        // set the adapter for the scores list (gameCountListView) rename??
        val scoresAdapter = ListViewAdapter(this, mScoresList)
        gameCountListView.adapter = scoresAdapter

        // dummy to test the gameCountListView adapter
        pauseGameButton.setOnClickListener {
            val item1: Model = Model(0, 1, 2, 3,4)
            mScoresList.add(item1)
            scoresAdapter.notifyDataSetChanged()
            gameCountListView.setSelection(scoresAdapter.count -1)
        }

        // dialog to choose the first player
        chooseFirstPlayer()


    }

    private fun chooseFirstPlayer(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Who will start?")

        builder.setView(LayoutInflater.from(this).inflate(R.layout.firstplayerlayout,null))
        builder.setItems(mPlayers.toTypedArray(), DialogInterface.OnClickListener{ _, pos ->

            // sort the player list such that the order remains but the chosen player starts
            mFirstPlayerIndex = pos
            mFirstPlayerName = mPlayers[mFirstPlayerIndex]

            for (i in 0 until mNumPlayers){
                var sortId = (i+mFirstPlayerIndex)%(mNumPlayers)
                mPlayersSorted[i] = mPlayers[sortId]
            }
            gamePlayer1TextView.text = mPlayersSorted[0]
            gamePlayer2TextView.text = mPlayersSorted[1]
            gamePlayer3TextView.text = mPlayersSorted[2]
            gamePlayer4TextView.text = mPlayersSorted[3]
        })


        builder.show()
    }


    // result contract to give back to main activity
    class Contract : ActivityResultContract<GameSetup, GameSetup>() {
        override fun createIntent(context: Context, input: GameSetup): Intent {
            val myIntent = Intent(context, GameActivity::class.java)
            myIntent.putExtra("players", input.players)
            myIntent.putExtra("numPlayers", input.numPlayers)
            myIntent.putExtra("time", input.time)
            myIntent.putExtra("winner", "")
            myIntent.putExtra("totals", input.showTotal)
    return myIntent
        }

        override fun parseResult(resultCode: Int, intent: Intent?) = GameSetup(
            players = ArrayList(intent?.getStringArrayListExtra("players")),
            numPlayers = intent?.getIntExtra("numPlayers", 2),
            time = intent?.getIntExtra("time", 0),
            winner = intent?.getStringExtra("winner"),
            showTotal = intent?.getBooleanExtra("totals", true)
        )
    }
}

