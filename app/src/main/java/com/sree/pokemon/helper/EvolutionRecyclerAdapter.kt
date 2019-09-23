package com.sree.pokemon.helper

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.sree.pokemon.R
import com.sree.pokemon.database.EvolutionEntity
import com.sree.pokemon.view.PokemonDetailsFragment
import com.sree.pokemon.viewmodel.PokemonDetailsViewmodel
import com.sree.pokemon.viewmodel.PokemonSharedViewmodel

class EvolutionRecyclerAdapter(
    private val onDetailsFragmentInteractionListener: PokemonDetailsFragment.OnDetailsFragmentInteractionListener?,
    private val pokemonDetailsViewmodel: PokemonDetailsViewmodel,
    private val pokemonSharedViewmodel: PokemonSharedViewmodel): RecyclerView.Adapter<EvolutionRecyclerAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_evolution,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (pokemonDetailsViewmodel.getEvolution().isNotEmpty())
            pokemonDetailsViewmodel.getEvolution().size
        else
            0
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val evolution = getItem(position)
        holder.textViewTitle.text = (evolution.number + " - "+ evolution.name)
        if (pokemonSharedViewmodel.checkInternalDirForImage(evolution.name+".png")){
            Glide.with(pokemonSharedViewmodel.applicationContext)
                .asBitmap()
                .load(pokemonSharedViewmodel.getImageUri(evolution.name+".png"))
                .apply(RequestOptions.overrideOf(180,180))
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.imageViewEvolution)
        }else{
            Glide.with(pokemonSharedViewmodel.applicationContext)
                .asBitmap()
                .load(evolution.image)
                .apply(RequestOptions.overrideOf(180, 180))
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {
                        Log.i("onLoadCleared", "onLoadCleared called in glide image")
                    }

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        //save data to storage
                        pokemonSharedViewmodel.saveImageToInternalStorage(evolution.name+".png", resource)
                        holder.imageViewEvolution.setImageBitmap(resource)
                    }
                })
        }
        holder.itemView.setOnClickListener{
            onDetailsFragmentInteractionListener?.onDetailsFragmentInteraction(evolution.name!!)
        }
    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
        val textViewTitle= view.findViewById<TextView>(R.id.textView_evolution_name)
        val imageViewEvolution = view.findViewById<ImageView>(R.id.imageView_evolution)
    }

    private fun getItem(position: Int):EvolutionEntity= pokemonDetailsViewmodel.getEvolution()[position]

}
