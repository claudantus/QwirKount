package com.example.qwirkount
// http://android-delight.blogspot.com/2015/12/tablelayout-like-listview-multi-column.html?m=1

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat


class ListViewAdapter(var activity: Activity, var productList: MutableList<ScoreModel>) :
    BaseAdapter() {
    override fun getCount(): Int {
        return productList.size
    }

    override fun getItem(position: Int): Any {
        return productList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private inner class ViewHolder {
        var mRound: TextView? = null
        var mScore1: TextView? = null
        var mScore2: TextView? = null
        var mScore3: TextView? = null
        var mScore4: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView: View? = convertView
        val holder: ViewHolder
        val inflater = activity.layoutInflater

        val bgColor = if (position%2==1){
            ContextCompat.getColor(activity, R.color.colorBG1)
        } else{
    //            convertView?.setBackgroundColor(R.color.colorBG2)
            ContextCompat.getColor(activity, R.color.colorBG2)
        }

        convertView?.setBackgroundColor(bgColor)


        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_row, null)
            holder = ViewHolder()
            holder.mRound = convertView.findViewById(R.id.Round)
            holder.mScore1 = convertView.findViewById(R.id.Score1)
            holder.mScore2 = convertView.findViewById(R.id.Score2)
            holder.mScore3 = convertView.findViewById(R.id.Score3)
            holder.mScore4 = convertView.findViewById(R.id.Score4)

            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }
        val item = productList[position]
        holder.mRound?.text = item.getRound().toString()
        holder.mScore1?.text = item.getScore1().toString()
        holder.mScore2?.text = item.getScore2().toString()
        holder.mScore3?.text = item.getScore3().toString()
        holder.mScore4?.text = item.getScore4().toString()

        return convertView
    }
}