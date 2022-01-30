package com.example.qwirkount

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {
    // players from the selection in main activity
    private lateinit var mPlayers: MutableList<String>
    // sorted list of players (will be filled when first player is selected)
    private lateinit var mPlayersSorted: MutableList<String>
    private lateinit var mScoresAdapter: ListViewAdapter
    private lateinit var mTotalsAdapter: ListViewAdapter

    private val mScoresList: ArrayList<ScoreModel> = ArrayList<ScoreModel>()
    private val mTotalsList: ArrayList<ScoreModel> = ArrayList<ScoreModel>()
    private val mTotals: ArrayList<Int> = arrayListOf(0,0,0,0)


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
    // index of current player
    private var mCurrentPlayerIndex: Int = 0
    // name of the starting player
    private var mFirstPlayerName: String = ""
    // name of the winner
    private var mWinner: String = ""
    // current row
    private var mCurrentScore: ScoreModel = ScoreModel(0, 0, 0, 0, 0)

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
        mScoresAdapter = ListViewAdapter(this, mScoresList)
        gameCountListView.adapter = mScoresAdapter

        // set the adapter for the totals list
        mTotalsList.add(mCurrentScore)
        mTotalsAdapter = ListViewAdapter(this, mTotalsList)
        totalsListView.adapter = mTotalsAdapter

        // dummy to test the gameCountListView adapter
        pauseGameButton.setOnClickListener {
            var a = 1
        }

        gameStartButton.setOnClickListener {
            gameDialog()
        }
        // dialog to choose the first player
        chooseFirstPlayer()


    }

    private fun chooseFirstPlayer(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Who will start?")

        // show listview with all selected players
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



    private fun gameDialog(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val currentPlayer: String = mPlayersSorted[mCurrentPlayerIndex]

        builder.setTitle("Round ${mRound}. It's ${currentPlayer}'s turn!")
        // Set up the input
        val input = EditText(this)
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.hint = 0.toString()
        input.inputType = InputType.TYPE_CLASS_NUMBER
        builder.setView(input)


        // Set up the buttons
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, _ ->
            var currentScore = input.text.toString()
            if (currentScore.isEmpty()){
                dialog.cancel()
            }

            // Here you get get input text from the Edittext
            mCurrentScore.setScore(mCurrentPlayerIndex, currentScore.toInt())
            mTotals[mCurrentPlayerIndex] += currentScore.toInt()


            if (mCurrentPlayerIndex == mNumPlayers-1){
                mScoresList.add(mCurrentScore)
                mScoresAdapter.notifyDataSetChanged()

                if (mShowTotals){
                    mTotalsList[0] = ScoreModel(mRound,mTotals[0],mTotals[1],mTotals[2],mTotals[3])
                    mTotalsAdapter.notifyDataSetChanged()
                }

                mCurrentPlayerIndex = 0
                mRound += 1


                mCurrentScore = ScoreModel(mRound,0,0,0,0)

            }
            else{
                mCurrentPlayerIndex += 1
            }


            gameDialog()

            }
        )
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

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

