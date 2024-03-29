package com.example.skincancerdetector.ui.analysis

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.skincancerdetector.R
import com.example.skincancerdetector.data.Disease
import com.example.skincancerdetector.data.ScanData
import com.example.skincancerdetector.databinding.FragmentAnalysisBinding
import com.example.skincancerdetector.model.AnalysisVM

class AnalysisFragment : Fragment() {
    private lateinit var binding : FragmentAnalysisBinding
    private lateinit var adapter : AnalysisAdapter
    private lateinit var analysisViewModel : AnalysisVM
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAnalysisBinding.inflate(inflater,container,false)
        binding.rvScans.layoutManager = LinearLayoutManager(this.context)

        analysisViewModel = ViewModelProvider(requireActivity())[AnalysisVM::class.java]

        analysisViewModel.diseases.observe(requireActivity()){
            if(it!=null) {
                val scan = analysisViewModel.currentScan
                if(scan!=null) {
                    displayData(it,scan)
                }
            }

            binding.rvScans.adapter = adapter
        }



        // Inflate the layout for this fragment
        return binding.root
    }

    private fun displayData(data:List<Disease>, scan: ScanData){

        val mostLikely = findLargestKeyValuePair(scan.result)
        binding.tvMostLikely.text = data.find { disease -> disease.id == mostLikely?.key }?.name
        binding.progressBar2.progress = (mostLikely?.value?.times(100))?.toInt()?:0
        binding.tvPercent.text = String.format("%.2f%%", mostLikely?.value?.times(100))
        Glide.with(this)
            .load(scan.imageUrl)
            .circleCrop()
            .into(binding.ivAnalysis)
        adapter = AnalysisAdapter(scan,data)
        adapter.setOnItemClickCallback(object : AnalysisAdapter.OnItemClickCallback {
            override fun onItemClicked(data: String) {
                val bundle = Bundle()
                bundle.putString("key", data)
                val fragment = DetailFragment()
                fragment.arguments = bundle

                findNavController().navigate(R.id.detailFragment, bundle)
            }
        })
    }

    fun findLargestKeyValuePair(map: Map<String, Float>): Map.Entry<String, Float>? {
        var maxEntry: Map.Entry<String, Float>? = null

        for (entry in map.entries) {
            if (maxEntry == null || entry.value > maxEntry.value) {
                maxEntry = entry
            }
        }

        return maxEntry
    }
}