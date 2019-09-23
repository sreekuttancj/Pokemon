package com.sree.pokemon.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.sree.pokemon.R
import com.sree.pokemon.viewmodel.PokemonSharedViewmodel

class MainActivity : AppCompatActivity(), PokemonListFragment.OnListFragmentInteractionListener,
PokemonDetailsFragment.OnDetailsFragmentInteractionListener{

    private lateinit var toolbar: Toolbar
    private lateinit var pokemonSharedViewmodel: PokemonSharedViewmodel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpView()
        pokemonSharedViewmodel = ViewModelProviders.of(this).get(PokemonSharedViewmodel::class.java)
    }

    private fun setUpView(){
        toolbar =findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    //list item click
    override fun onListFragmentInteraction(position: Int) {
        val bundle = Bundle()
        bundle.putInt("position",position)
        bundle.putString("from","parent")
        findNavController(R.id.nav_host_fragment).navigate(R.id.action_pokemonListFragment_to_pokemonDetailsFragment,bundle)
    }

    //when user click evolution item then call this
    override fun onDetailsFragmentInteraction(name:String) {
        val bundle = Bundle()
        bundle.putString("from","evolution")
        bundle.putString("name",name)
        findNavController(R.id.nav_host_fragment).navigate(R.id.action_pokemonDetailsFragment_self,bundle)
    }
}
