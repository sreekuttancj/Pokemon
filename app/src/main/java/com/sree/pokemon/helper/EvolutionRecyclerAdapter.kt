package com.sree.pokemon.helper

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sree.pokemon.R
import com.sree.pokemon.view.PokemonDetailsFragment

class EvolutionRecyclerAdapter(private val onDetailsFragmentInteractionListener: PokemonDetailsFragment.OnDetailsFragmentInteractionListener?): RecyclerView.Adapter<EvolutionRecyclerAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_evolution,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.itemView.setOnClickListener{
            Log.i("check_evolution","clicked")
            onDetailsFragmentInteractionListener?.onDetailsFragmentInteraction(position)
        }
    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
    }
}
