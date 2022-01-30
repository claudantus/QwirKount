package com.example.qwirkount
//http://android-delight.blogspot.com/2015/12/tablelayout-like-listview-multi-column.html?m=1

class ScoreModel(private val round: Int, private val score1: Int, private val score2: Int, private val score3: Int, private val score4: Int) {
    private var mRound: Int = round
    private var mScore1: Int = score1
    private var mScore2: Int = score2
    private var mScore3: Int = score3
    private var mScore4: Int = score4


    public fun getRound(): Int {
        return mRound
    }

    public fun getScore1(): Int {
        return mScore1
    }

    public fun getScore2(): Int {
        return mScore2
    }

    public fun getScore3(): Int {
        return mScore3
    }

    public fun getScore4(): Int {
        return mScore4
    }

    public fun setRound(round: Int){
        mRound = round
    }

    public fun setScore(player: Int, score: Int){
        when (player) {
            0 -> {
                mScore1 = score
            }
            1 -> {
                mScore2 = score
            }
            2 -> {
                mScore3 = score
            }
            3 -> {
                mScore4 = score
            }
        }

    }



}