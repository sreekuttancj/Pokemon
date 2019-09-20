package com.sree.pokemon.helper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sree.pokemon.R

class AttackRecyclerVIewAdapter: RecyclerView.Adapter<AttackRecyclerVIewAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_attack_list,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    }

    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {

    }
}