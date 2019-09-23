package com.sree.pokemon.helper

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition


import com.sree.pokemon.view.PokemonListFragment.OnListFragmentInteractionListener
import com.sree.pokemon.R
import com.sree.pokemon.model.Pokemon
import com.sree.pokemon.viewmodel.PokemonListViewmodel
import com.sree.pokemon.viewmodel.PokemonSharedViewmodel

/**
 * [RecyclerView.Adapter] that can display a Pokemon and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */

class PokemonRecyclerViewAdapter(
    private val mListener: OnListFragmentInteractionListener?,
    private val pokemonSharedViewmodel: PokemonSharedViewmodel,
    private val pokemonListViewmodel: PokemonListViewmodel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var VIEW_ITEM = 1
    private var VIEW_FOOTER = 2
    private var showFooter: Boolean = false
    private val myHandler =Handler()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        if (viewType == VIEW_ITEM){
            view= LayoutInflater.from(parent.context).inflate(R.layout.layout_pokemon_list_item,parent,false)
            return MyViewHolder(view)
        }else if (viewType == VIEW_FOOTER){
            view= LayoutInflater.from(parent.context).inflate(R.layout.layout_footer,parent,false)
            return FooterViewHolder(view)
        }
        throw IllegalArgumentException("Invalid ViewType: $viewType")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FooterViewHolder) {
            if (showFooter) {
                holder.progressBarFooter.visibility = View.VISIBLE
            } else {
                holder.progressBarFooter.visibility = View.GONE
            }
            if (!showFooter && position==150)
                holder.progressBarFooter.visibility = View.GONE
        }
        if (holder is MyViewHolder) {
            val pokemon =  getItem(position)
            holder.textViewTitle.text =(pokemon.number +" - "+pokemon.name)

            if (pokemonSharedViewmodel.checkInternalDirForImage(pokemon.name+".png")){
                Glide.with(pokemonSharedViewmodel.applicationContext)
                    .asBitmap()
                    .load(pokemonSharedViewmodel.getImageUri(pokemon.name+".png"))
                    .apply(RequestOptions.overrideOf(180,180))
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.imageViewPokemon)
            }else{
                Glide.with(pokemonSharedViewmodel.applicationContext)
                    .asBitmap()
                    .load(pokemon.image)
                    .apply(RequestOptions.overrideOf(180, 180))
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onLoadCleared(placeholder: Drawable?) {
                            Log.i("onLoadCleared", "onLoadCleared called in glide image")
                        }

                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            //save data to storage
                            pokemonSharedViewmodel.saveImageToInternalStorage(pokemon.name+".png", resource)
                            holder.imageViewPokemon.setImageBitmap(resource)
                        }
                    })
            }
            holder.itemView.setOnClickListener{
                mListener?.onListFragmentInteraction(position)
            }
        }
    }


    override fun getItemCount(): Int{
        return if(pokemonSharedViewmodel.getPokemonList().isEmpty())
            0
        else {
            pokemonSharedViewmodel.getPokemonList().size + 1
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position != 0 && position == itemCount-1) VIEW_FOOTER
        else VIEW_ITEM
    }

     class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val textViewTitle = view.findViewById<TextView>(R.id.textView_title)
         val imageViewPokemon =view.findViewById<ImageView>(R.id.imageView_pokemon)
    }

     class FooterViewHolder(view: View): RecyclerView.ViewHolder(view){
        val progressBarFooter: ProgressBar = view.findViewById(R.id.progressbar_footer)
    }
    fun showLoading(status: Boolean) {
        showFooter = status
    }

    fun loadingStatus(): Boolean = showFooter

    fun onLoadMore(){
        showLoading(true)
        pokemonListViewmodel.setInitialFetch(false)
        pokemonListViewmodel.setNormalFetch(true)
        //151 is the last item
        if (pokemonSharedViewmodel.getPokemonList().size!=151) {
            myHandler.postDelayed({
                showLoading(false)
                val generatedNameList = pokemonListViewmodel.generatePokemonNameForNormalFetch(pokemonSharedViewmodel)
                if (ConnectivityHelper.isConnectedToNetwork(pokemonSharedViewmodel.applicationContext)){
                    generatedNameList.forEach { name ->
                        pokemonListViewmodel.fetchPokemonFromServer(name)
                    }
            }else{
                    Toast.makeText(pokemonSharedViewmodel.applicationContext,pokemonSharedViewmodel.applicationContext.resources.getString(R.string.no_network_connection),Toast.LENGTH_SHORT).show()

                }
            }, 1500)
        }else{
            Toast.makeText(pokemonSharedViewmodel.applicationContext,"Hai I am Mew,I am the last one",Toast.LENGTH_SHORT).show()
        }
    }

    fun invalidate(){
        notifyDataSetChanged()
    }

    private fun getItem(position: Int): Pokemon{
        return pokemonSharedViewmodel.getPokemonList()[position]
    }
}
