package edu.umsl.tyler.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData

class LapViewModel(): ViewModel() {

    var lapList: MutableLiveData<ArrayList<String>> = MutableLiveData() // Mutable live data for the lap list
    var differenceLapList = ArrayList<String>() // The difference which will only change when the lap list changes
    var numLap = ArrayList<Int>() // The lap list numbers to display which lap this is
    private var list = ArrayList<String>() // List create the new list lapList

    fun setLaps(lap: String, differenceLap: String) { // Setting the laps and adding to each of the new arrays
        list.add(lap)
        lapList.value = list // replace laplist
        differenceLapList.add(differenceLap) // add the differencelap
        numLap.add(list.size) // add the size
        lapList.postValue(list) // say that the laplist changed
    }

    fun resetLaps() { // Reset of all lists
        val newList = ArrayList<String>()
        list = newList
        lapList.postValue(list)
    }
}

