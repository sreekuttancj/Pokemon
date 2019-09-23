package com.sree.pokemon.helper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sree.pokemon.R

class AttackRecyclerVIewAdapter(private val specialPower:ArrayList<String>): RecyclerView.Adapter<AttackRecyclerVIewAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_attack_list,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return specialPower.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textViewSpecialPower.text = getItem(position)
    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textViewSpecialPower = view.findViewById<TextView>(R.id.textView_specialPower)
    }

    private fun getItem(position: Int):String = specialPower[position]
}