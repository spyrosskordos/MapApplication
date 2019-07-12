package com.threenitas.map.adapters


import android.content.Context
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.threenitas.map.R
import com.threenitas.map.api.APIClient
import com.threenitas.map.models.Prediction
import java.util.ArrayList

class PlacesAutoCompleteAdapter( context: Context, private val predictions: MutableList<Prediction>?) :
    ArrayAdapter<Prediction>(context, R.layout.result_layout, predictions) {
  //  private var resultList: MutableList<Prediction> = ArrayList()
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(R.layout.result_layout, null)
        if (predictions != null && predictions.size > 0) {
            val prediction = predictions[position]
                val textViewName =view!!.findViewById<TextView>(R.id.textViewName)

            textViewName.setText(prediction.getDescription())
        }
        return view
    }
//    override fun getItem(index: Int): Prediction {
//        return resultList[index]
//    }


    override fun getFilter(): Filter {
        return PlacesAutoCompleteFilter(this, context)
    }

    private inner class PlacesAutoCompleteFilter(
        private val placesAutoCompleteAdapter: PlacesAutoCompleteAdapter,
        private val context: Context
    ) : Filter() {

        override fun performFiltering(charSequence: CharSequence?): Filter.FilterResults? {
            try {
                placesAutoCompleteAdapter.predictions!!.clear()
                val filterResults = Filter.FilterResults()
                if (charSequence == null || charSequence.length == 0) {
                    filterResults.values = ArrayList<Prediction>()
                    filterResults.count = 0
                } else {
                    val googleMapAPI = APIClient.getClient
                    val predictions = googleMapAPI.getPlacesAutoComplete(
                        charSequence.toString(),
                        "geocode",
                        "en",
                        context.getString(R.string.GOOGLE_MAPS_API_KEY)
                    ).execute().body()
                    filterResults.values = predictions!!.getPredictions()
                    filterResults.count = predictions!!.getPredictions().size
                }
                return filterResults
            } catch (e: Exception) {
                return null
            }

        }

        override fun publishResults(charSequence: CharSequence?, filterResults: Filter.FilterResults) {
            Log.d("test","tttttttt")
            placesAutoCompleteAdapter.predictions!!.clear()
            placesAutoCompleteAdapter.predictions.addAll(filterResults.values as List<Prediction>)
            placesAutoCompleteAdapter.notifyDataSetChanged()
        }

        override fun convertResultToString(resultValue: Any): CharSequence {
            val prediction = resultValue as Prediction
            return prediction.getDescription()
        }
    }

}