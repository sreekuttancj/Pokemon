package com.sree.pokemon.view

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.sree.pokemon.R
import com.sree.pokemon.helper.ConnectivityHelper
import com.sree.pokemon.helper.ConnectivityHelper.Companion.isConnectedToNetwork
import com.sree.pokemon.helper.DividerItemDecoration
import com.sree.pokemon.helper.PokemonRecyclerViewAdapter
import com.sree.pokemon.viewmodel.PokemonListViewmodel
import com.sree.pokemon.viewmodel.PokemonSharedViewmodel
import java.util.*

/**
 * A fragment representing a list of pokemon.
 * Activities containing this fragment MUST implement the
 * [PokemonListFragment.OnListFragmentInteractionListener] interface.
 */
class PokemonListFragment : Fragment() {

    private var columnCount = 2
    private var listener: OnListFragmentInteractionListener? = null
    private lateinit var pokemonSharedViewmodel:PokemonSharedViewmodel
    private lateinit var pokemonListViewmodel: PokemonListViewmodel
    private lateinit var pokemonRecyclerViewAdapter:PokemonRecyclerViewAdapter
    private lateinit var progrssDialog:ProgressDialog

    private var totalItemCount =-1
    private var lastVisibleItem =-1
    private val visibleThreshold = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        //initialise view model
        activity.let {
            pokemonSharedViewmodel = ViewModelProviders.of(it!!).get(PokemonSharedViewmodel::class.java)
        }
        progrssDialog = ProgressDialog(requireContext())
        pokemonListViewmodel = ViewModelProviders.of(this).get(PokemonListViewmodel::class.java)

        //call api for pokemon name list
        if (pokemonListViewmodel.isTableEmpty()){
            if (isConnectedToNetwork(requireContext())) {
                showProgressDialog()
                pokemonListViewmodel.setInitialFetch(true)
                pokemonListViewmodel.fetchPokemonNameListFromServer()
            }
        }else{
            //if table is not empty no need for api call, simulate api call
            pokemonListViewmodel.setInitialFetch(true)
            pokemonListViewmodel.setPokemonListApiStatus("success")
        }
        /**
         * called when fetch pokemon name list from api or database
         */

        pokemonListViewmodel.getPokemonListApiStatus().observe(this, Observer {
            it.let{
                //generate names which are not fetched already
                val generatedNameList = pokemonListViewmodel.generatePokemonNameForInitialFetch()
                //call api for pokemon data
                generatedNameList.forEach {name->
                    pokemonListViewmodel.fetchPokemonFromServer(name)
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        //for a fresh start
        pokemonListViewmodel.clearIncrementPokemonNameGeneratorLiveDataStatus()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_pokemon_list, container, false)
        setUpView(view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        /**
         * api counter and api response are this observer will activate
         * load data from db when conditions are met
         */
        pokemonListViewmodel.getIncrementPokemonNameGeneratorLiveDataStatus().observe(this, Observer {
            if (pokemonListViewmodel.getApiCounter() == it&& pokemonListViewmodel.isInitialFetch()){
                pokemonSharedViewmodel.loadPokemonListFromDB("initial_fetch")
            }
            if (pokemonListViewmodel.getApiCounter() == it && pokemonListViewmodel.isNormalFetch()){
                pokemonSharedViewmodel.loadPokemonListFromDB( "normal_fetch")
                pokemonListViewmodel.setInitialFetch(false)
            }
            //last item
            if (pokemonSharedViewmodel.getPokemonList().size==150){
                pokemonSharedViewmodel.loadPokemonListFromDB("normal_fetch")
                pokemonRecyclerViewAdapter.invalidate()
            }
        })

        pokemonSharedViewmodel.getPokmonListLiveData().observe(this, Observer {
            pokemonRecyclerViewAdapter.showLoading(false)
            dismissProgrssDialog()
            pokemonRecyclerViewAdapter.invalidate()
        })
    }

    private fun setUpView(view: View){
        val noNetworkLayout = view.findViewById<RelativeLayout>(R.id.no_network)
        val gridLayoutManager = GridLayoutManager(context, columnCount)
        val recyclerViewList = view.findViewById<RecyclerView>(R.id.recycler_pokemon_list)
        val buttonRefresh = view.findViewById<Button>(R.id.button_refresh)

        pokemonRecyclerViewAdapter = PokemonRecyclerViewAdapter(listener,pokemonSharedViewmodel,pokemonListViewmodel)
        //last item is footer
        // so need to combine grid,
        // if content size is odd then last item need to combine grid
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                //first and last item has combined span
                return when (position) {
                    gridLayoutManager.itemCount - 1 -> 2
                    gridLayoutManager.itemCount - 2 -> {
                        if (gridLayoutManager.itemCount % 2 == 0)
                            return 2
                        else
                            1
                    }
                    else -> 1
                }
            }
        }

        // Set recycler view and adapter
            with(recyclerViewList) {
                addItemDecoration(DividerItemDecoration(8))
                layoutManager = gridLayoutManager
                adapter = pokemonRecyclerViewAdapter
                setHasFixedSize(true)
                //check end of the view and load next set of item
                addOnScrollListener(object : RecyclerView.OnScrollListener(){
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        totalItemCount = gridLayoutManager.itemCount
                        lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition()
                        if (!pokemonRecyclerViewAdapter.loadingStatus() && totalItemCount > 1 && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                            pokemonRecyclerViewAdapter.onLoadMore()
                        }
                    }
                })
            }

        //network handling ui
        if (pokemonListViewmodel.isTableEmpty()){
            if (isConnectedToNetwork(requireContext())){
                recyclerViewList.visibility = View.VISIBLE
                noNetworkLayout.visibility = View.GONE
            }else{
                recyclerViewList.visibility = View.GONE
                noNetworkLayout.visibility = View.VISIBLE
            }
        }
        //no network refresh button click
        buttonRefresh.setOnClickListener{
            if (isConnectedToNetwork(requireContext())){
                noNetworkLayout.visibility = View.GONE
                recyclerViewList.visibility = View.VISIBLE
                //call api for pokemon name list
                if (pokemonListViewmodel.isTableEmpty()){
                    if (isConnectedToNetwork(requireContext())){
                        pokemonListViewmodel.setInitialFetch(true)
                        showProgressDialog()
                        pokemonListViewmodel.fetchPokemonNameListFromServer()
                    }

                }else{
                    //simulate api call
                    pokemonListViewmodel.setInitialFetch(true)
                    pokemonListViewmodel.setPokemonListApiStatus("success")
                }
            }else{
                Toast.makeText(requireContext(),resources.getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     */
    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(position:Int)
    }

    private fun showProgressDialog() {
        progrssDialog.setMessage(getString(R.string.loading))
        if (!progrssDialog.isShowing)
        progrssDialog.show()
    }
    private fun dismissProgrssDialog(){
        if (progrssDialog.isShowing)
        progrssDialog.dismiss()
    }
}
