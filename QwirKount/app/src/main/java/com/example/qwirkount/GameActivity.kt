package com.example.qwirkount

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.correctscore_dialog.view.*

class GameActivity : AppCompatActivity() {
    // players from the selection in main activity
    private lateinit var mPlayers: MutableList<String>
    // sorted list of players (will be filled when first player is selected)
    private lateinit var mPlayersSorted: MutableList<String>
    private lateinit var mScoresAdapter: ListViewAdapter
    private lateinit var mTotalsAdapter: ListViewAdapter

    private val mScoresList: MutableList<ScoreModel> = mutableListOf()
    private val mTotalsList: MutableList<ScoreModel> = mutableListOf()
    private var mTotals: ArrayList<Int> = arrayListOf(0,0,0,0)


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
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Stop the game?")

        // Set up the buttons
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
            // Here you get get input text from the Edittext
            val resultIntent = Intent()
            resultIntent.putExtra("time", mTime)
            resultIntent.putExtra("players", ArrayList(mPlayers))
            resultIntent.putExtra("numPlayers", mNumPlayers)
            resultIntent.putExtra("winner", mWinner)
            setResult(Activity.RESULT_CANCELED, resultIntent)
            finish()
        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
        builder.show()
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

        gameCountListView.setOnItemClickListener{ _, _, position, _ ->
            // dialog to correct score from chosen line
            correctScoreDialog(position)
        }

        resetGameButton.setOnClickListener {
            refreshGameDialog()
        }
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

    private fun refreshGameDialog(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Refresh the game?")

        // Set up the buttons
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
            // Refresh the game
            mRound = 0
            mScoresList.clear()
            mTotals = arrayListOf(0,0,0,0)
            mTotalsList[0] = ScoreModel(mRound, mTotals[0], mTotals[1], mTotals[2], mTotals[3])

            mScoresAdapter.notifyDataSetChanged()
            mTotalsAdapter.notifyDataSetChanged()
        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() })
        builder.show()
    }

    private fun correctScoreDialog(lineIndex: Int){
        //Inflate the dialog with custom view
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.correctscore_dialog, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("Score Correction Form")
        //show dialog
        val mAlertDialog = mBuilder.show()
        val mList = ArrayList(mPlayersSorted)

        val mDialogAdapter = ArrayAdapter(this,
            R.layout.listview_item, mList)
        mDialogView.dialogPlayerListView.adapter = mDialogAdapter

        var mClickedList = List<Boolean>(mList.size) { false  }.toMutableList()
        var mPlayerChosenIndex = -1

        mDialogView.dialogPlayerListView.setOnItemClickListener{ _, _, position, _ ->
            val state = !mClickedList[position]
            mClickedList = List<Boolean>(mList.size) { false  }.toMutableList()
            mClickedList[position] = state

            (0 until mList.size).forEach { i ->
                mDialogView.dialogPlayerListView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT)
            }

            if (state){
                mPlayerChosenIndex = position
                mDialogView.dialogPlayerListView.getChildAt(position).setBackgroundColor(resources.getColor(R.color.colorBG))
            }
        }

        mDialogView.dialogCancelButton.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()
        }
        mDialogView.dialogOkButton.setOnClickListener {
            //dismiss dialog
            var chosenScore : ScoreModel = mScoresList[lineIndex]
            var newScore = mDialogView.dialogNewScoreEditText.text.toString()

            if (newScore.isEmpty()){
                mAlertDialog.dismiss()
            }

            val oldScore = chosenScore
            chosenScore.setScore(mPlayerChosenIndex, newScore.toInt())
            mScoresList[lineIndex] = chosenScore
            mScoresAdapter.notifyDataSetChanged()

            if (mShowTotals){
                mTotalsList[0] = ScoreModel(mRound,mTotals[0] - oldScore.getScore1() + chosenScore.getScore1(),
                    mTotals[1] - oldScore.getScore2() + chosenScore.getScore2(),
                    mTotals[2] - oldScore.getScore3() + chosenScore.getScore3(),
                    mTotals[3]- oldScore.getScore4() + chosenScore.getScore4())
                mTotalsAdapter.notifyDataSetChanged()
            }
            mAlertDialog.dismiss()
        }
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
            val currentScore: String = input.text.toString()

            if (currentScore.trim().isEmpty()) {
                Toast.makeText(this, "Invalid Score: Must not be empty!", Toast.LENGTH_SHORT).show()
                dialog.cancel()
            } else {
                // Here you get get input text from the Edittext
                mCurrentScore.setScore(mCurrentPlayerIndex, currentScore.toInt())
                mTotals[mCurrentPlayerIndex] += currentScore.toInt()

                if (mCurrentPlayerIndex == mNumPlayers - 1) {
                    mScoresList.add(mCurrentScore)
                    mScoresAdapter.notifyDataSetChanged()

                    if (mShowTotals) {
                        mTotalsList[0] =
                            ScoreModel(mRound, mTotals[0], mTotals[1], mTotals[2], mTotals[3])
                        mTotalsAdapter.notifyDataSetChanged()
                    }

                    mCurrentPlayerIndex = 0
                    mRound += 1

                    mCurrentScore = ScoreModel(mRound, 0, 0, 0, 0)

                } else {
                    mCurrentPlayerIndex += 1
                }
                gameDialog()
            }
        }
        )
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() })

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

