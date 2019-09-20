package com.sree.pokemon.view


import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

import com.sree.pokemon.R
import com.sree.pokemon.helper.AttackRecyclerVIewAdapter
import com.sree.pokemon.helper.DividerItemDecoration
import com.sree.pokemon.helper.EvolutionRecyclerAdapter

/**
 * Display pokemon details
 *
 */
class PokemonDetailsFragment : Fragment() {

    private var onDetailsFragmentInteractionListener: OnDetailsFragmentInteractionListener?=null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnDetailsFragmentInteractionListener)
            onDetailsFragmentInteractionListener =context
     else
        throw RuntimeException("$context must implement onDetailsFragmentInteractionListener")

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_pokemon_details, container, false)
        setUpView(view)

        return view
    }

    private fun setUpView(view: View){
        val recyclerViewAttack = view.findViewById(R.id.recyclerview_attack) as RecyclerView
        with(recyclerViewAttack){
            layoutManager = GridLayoutManager(requireContext(),3)
            addItemDecoration(DividerItemDecoration(8))
            adapter = AttackRecyclerVIewAdapter()
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
        }

        val recyclerViewEvolution = view.findViewById(R.id.recyclerView_evolution) as RecyclerView
        with(recyclerViewEvolution){
            layoutManager = LinearLayoutManager(requireContext())
            adapter=EvolutionRecyclerAdapter(onDetailsFragmentInteractionListener)
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
        }
    }

    interface OnDetailsFragmentInteractionListener{
        fun onDetailsFragmentInteraction(position:Int)

    }

    override fun onDetach() {
        super.onDetach()
        onDetailsFragmentInteractionListener = null
    }
}
