package com.example.qwirkount
//http://android-delight.blogspot.com/2015/12/tablelayout-like-listview-multi-column.html?m=1

class Model(private val round: Int, private val score1: Int, private val score2: Int, private val score3: Int, private val score4: Int) {
    private val mRound: Int = round
    private val mScore1: Int = score1
    private val mScore2: Int = score2
    private val mScore3: Int = score3
    private val mScore4: Int = score4


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







}