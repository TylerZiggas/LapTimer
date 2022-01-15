package edu.umsl.tyler.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.umsl.tyler.databinding.LapFragmentBinding
import edu.umsl.tyler.databinding.LapListBinding

class LapFragment : Fragment() {

    private lateinit var viewModel: LapViewModel // Grab view model
    private lateinit var binding: LapFragmentBinding // Grab binding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = LapFragmentBinding.inflate(inflater, container, false)
        return binding.root // Return the binding for the lap
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(LapViewModel::class.java) // Grab the view model for using the recycler view

        binding.lapRecyclerView.layoutManager = LinearLayoutManager(activity)
        val adapter = LapListAdapter(viewModel.lapList.value ?: emptyList(), viewModel.numLap, viewModel.differenceLapList) // Grab the lists that will be displayed for the adapter
        binding.lapRecyclerView.adapter = adapter

        viewModel.lapList.observe(this) {// Observing the lapList so we know if a change happens
            adapter.lapList = it
            adapter.notifyDataSetChanged()
        }
    }

    inner class LapListAdapter(var lapList: List<String>, var numLap: List<Int>, var differenceLapList: List<String>) : RecyclerView.Adapter<LapViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LapViewHolder { // Creation of the view holder for the recycler view
            val inflater = LayoutInflater.from(parent.context)
            val binding = LapListBinding.inflate(inflater, parent, false)
            return LapViewHolder(binding)
        }

        override fun onBindViewHolder(holder: LapViewHolder, position: Int) { // Bind the positions
            holder.bind(lapList[position], numLap[position], differenceLapList[position])
        }

        override fun getItemCount(): Int = lapList.size // Getting the size to know what all to show
    }

    inner class LapViewHolder(private val binding: LapListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(lapList: String, numLap: Int, differenceLap: String) { // placing all of the information that was bound in the holder
            binding.differenceLapTime.text = differenceLap
            binding.lapNumTextView.text = numLap.toString()
            binding.lapTextView.text = lapList
        }
    }
}
