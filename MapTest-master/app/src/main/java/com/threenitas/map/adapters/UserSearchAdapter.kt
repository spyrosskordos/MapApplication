package com.threenitas.map.adapters

import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.maps.MapFragment
import com.threenitas.map.R
import com.threenitas.map.activities.MyApplication
import com.threenitas.map.models.UserSearch
import io.realm.OrderedRealmCollection
import kotlin.coroutines.coroutineContext
import com.google.android.gms.maps.SupportMapFragment
import com.threenitas.map.activities.MainActivity
import kotlinx.android.synthetic.main.history_results.view.*


class UserSearchAdapter(val searchList: OrderedRealmCollection<UserSearch>,val clickListener:(UserSearch)->Unit) : RecyclerView.Adapter<UserSearchAdapter.ViewHolder>(){

 override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
val v=LayoutInflater.from(parent.context).inflate(R.layout.history_results,parent,false)

  return ViewHolder(v)
 }

 override fun getItemCount(): Int {
 return  searchList.size
 }

 override fun onBindViewHolder(holder: ViewHolder, position: Int) {

   (holder as ViewHolder).bind(searchList[position], clickListener)

 }


inner class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){

 fun bind(search: UserSearch, clickListener: (UserSearch) -> Unit) {

  itemView.item_history.text=search.description
  itemView.setOnClickListener { clickListener(search)}

 }

 }


}