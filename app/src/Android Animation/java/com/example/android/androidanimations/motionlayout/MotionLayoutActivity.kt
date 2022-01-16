package com.example.android.androidanimations.motionlayout

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.androidanimations.R

class MotionLayoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_motion_layout)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.adapter = MainAdapter(data)
    }
}
//scrub bar

class MainAdapter(val data: List<Step>) : RecyclerView.Adapter<MainViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return MainViewHolder(view as CardView)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bind(data[position])
    }

}

class MainViewHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView) {
    val header: TextView = cardView.findViewById(R.id.header)
    val description: TextView = cardView.findViewById(R.id.description)
    val caption: TextView = cardView.findViewById(R.id.caption)

    fun bind(step: Step) {
        header.text = step.number
        description.text = step.name
        caption.text = step.caption
        val context = cardView.context
        cardView.setOnClickListener {
            val intent = Intent(context, step.activity.java)
            context.startActivity(intent)
        }
        val color = if (step.highlight) {
            getColor(R.color.secondaryLightColor, itemView.context)
        } else {
            getColor(R.color.primaryTextColor, itemView.context)
        }
        header.setTextColor(color)
        description.setTextColor(color)
    }

    fun getColor(@ColorRes colorResId: Int, context: Context): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.resources.getColor(colorResId, context.theme)
        } else {
            @Suppress("DEPRECATION")
            context.resources.getColor(colorResId)
        }
    }

}

