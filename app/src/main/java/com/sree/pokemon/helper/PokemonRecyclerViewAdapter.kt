package com.sree.pokemon.helper

import android.os.Handler
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar


import com.sree.pokemon.view.PokemonListFragment.OnListFragmentInteractionListener
import com.sree.pokemon.R
/**
 * [RecyclerView.Adapter] that can display a Pokemon and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */

class PokemonRecyclerViewAdapter(
    private val mListener: OnListFragmentInteractionListener?
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
        }
        if (holder is MyViewHolder) {

            holder.itemView.setOnClickListener{
                mListener?.onListFragmentInteraction(position)
            }
        }
    }

    override fun getItemCount(): Int =26

    override fun getItemViewType(position: Int): Int {
        return if (position != 0 && position == itemCount-1) VIEW_FOOTER
        else VIEW_ITEM
    }

     class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

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

        myHandler.postDelayed({
            showLoading(false)
            Log.i("check_loadmore","called load more")
        },1500)
    }
}
