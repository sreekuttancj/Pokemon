package com.sree.pokemon.view


import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

import com.sree.pokemon.R
import com.sree.pokemon.helper.AttackRecyclerVIewAdapter
import com.sree.pokemon.helper.ConnectivityHelper
import com.sree.pokemon.helper.DividerItemDecoration
import com.sree.pokemon.helper.EvolutionRecyclerAdapter
import com.sree.pokemon.model.Pokemon
import com.sree.pokemon.viewmodel.PokemonDetailsViewmodel
import com.sree.pokemon.viewmodel.PokemonSharedViewmodel

/**
 * Display pokemon details
 *
 */
class PokemonDetailsFragment : Fragment() {

    private var onDetailsFragmentInteractionListener: OnDetailsFragmentInteractionListener?=null
    private lateinit var pokemonSharedViewmodel: PokemonSharedViewmodel
    private lateinit var pokemonDetailsViewmodel: PokemonDetailsViewmodel
    private lateinit var pokemon:Pokemon

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnDetailsFragmentInteractionListener)
            onDetailsFragmentInteractionListener =context
     else
        throw RuntimeException("$context must implement onDetailsFragmentInteractionListener")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity.let {
            pokemonSharedViewmodel=ViewModelProviders.of(it!!).get(PokemonSharedViewmodel::class.java)
        }
        pokemonDetailsViewmodel = ViewModelProviders.of(this).get(PokemonDetailsViewmodel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_pokemon_details, container, false)

        if (arguments?.getString("from") == "parent"){
        //come to this when user click pokemon item from list
        val position = arguments?.getInt("position")
        if (position!=null){
            pokemon = pokemonSharedViewmodel.getPokemonByPosition(position)
            pokemonDetailsViewmodel.setParentName(pokemon.name!!)
        }
            setUpView(view)
        }else{
            //come to this when user click evolution item to get details
            //get name from bundle
            val name = arguments?.getString("name")
            if (pokemonDetailsViewmodel.isPokemonPresent(name!!)){
                pokemon = pokemonDetailsViewmodel.getPokemonByName(name)
                pokemonDetailsViewmodel.setParentName(pokemon.name!!)
                setUpView(view)
            }else{
                if (ConnectivityHelper.isConnectedToNetwork(requireContext()))
                pokemonDetailsViewmodel.fetchPokemonForEvolutionFromServer(name)
                else
                    Toast.makeText(requireContext(),resources.getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show()
            }
        }

        pokemonDetailsViewmodel.getPokemonForEvolutionApiStatusLiveData().observe(this, Observer {
            it.let {
                if (it=="success"){
                    pokemon = pokemonDetailsViewmodel.getPokemonByName(arguments?.getString("name")!!)
                    setUpView(view)
                }else{
                    //handle network response failure case
                    Toast.makeText(requireContext(),getString(R.string.warning), Toast.LENGTH_SHORT).show()
                }
            }
        })
        return view
    }

    private fun setUpView(view: View){
        val textViewTitle = view.findViewById<TextView>(R.id.textView_name)
        val imageViewPokemon = view.findViewById<ImageView>(R.id.imageView_pokemon)
        val textViewEvolutionWarning = view.findViewById<TextView>(R.id.textView_evolution_warning)
         val recyclerViewAttack = view.findViewById(R.id.recyclerview_attack) as RecyclerView
        val recyclerViewEvolution = view.findViewById(R.id.recyclerView_evolution) as RecyclerView

        textViewTitle.text = (pokemon.number + " - "+pokemon.name)
        with(recyclerViewAttack){
            layoutManager = GridLayoutManager(requireContext(),3)
            addItemDecoration(DividerItemDecoration(8))
            adapter = pokemon.specialPower?.let { AttackRecyclerVIewAdapter(it) }
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
        }
        with(recyclerViewEvolution){
            layoutManager = LinearLayoutManager(requireContext())
            adapter=EvolutionRecyclerAdapter(onDetailsFragmentInteractionListener,pokemonDetailsViewmodel,pokemonSharedViewmodel)
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
        }
        //set evolution warning based on value
        if (pokemon.evolution !=null && pokemon.evolution?.size!=0){
            textViewEvolutionWarning.visibility = View.GONE
            recyclerViewEvolution.visibility = View.VISIBLE
        }else{
            textViewEvolutionWarning.visibility = View.VISIBLE
            recyclerViewEvolution.visibility = View.GONE
        }

        //load image,first check locally
        if (pokemonSharedViewmodel.checkInternalDirForImage(pokemon.name+".png")){
            Glide.with(requireContext())
                .asBitmap()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .load(pokemonSharedViewmodel.getImageUri(pokemon.name+".png"))
                .into(imageViewPokemon)
        }else{
            //load it from server
            Glide.with(pokemonSharedViewmodel.applicationContext)
                .asBitmap()
                .load(pokemon.image)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {
                        Log.i("onLoadCleared", "onLoadCleared called in glide image")
                    }

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        //save data to storage
                        pokemonSharedViewmodel.saveImageToInternalStorage(pokemon.name+".png", resource)
                        imageViewPokemon.setImageBitmap(resource)
                    }
                })
        }

    }

    /**
     * call for evolution click
     */
    interface OnDetailsFragmentInteractionListener{
        fun onDetailsFragmentInteraction(name:String)
    }

    override fun onDetach() {
        super.onDetach()
        onDetailsFragmentInteractionListener = null
    }
}
