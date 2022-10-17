package dev.mfaheemezani.mvvm.ui.tophome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dev.mfaheemezani.mvvm.databinding.ActivityTopHomeStoriesBinding
import dev.mfaheemezani.mvvm.ui.NewYorkTimesViewModel
import dev.mfaheemezani.mvvm.data.network.response.Result

class TopHomeStoriesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTopHomeStoriesBinding
    private var topHomeStoriesAdapter: TopHomeItemsAdapter? = null
    private lateinit var viewModel: NewYorkTimesViewModel
    private var results = mutableListOf<Result>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopHomeStoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViewModel()
        initializeUI()
    }

    private fun initializeViewModel() {
        viewModel = ViewModelProvider(this)[NewYorkTimesViewModel::class.java]

        // Initialize observer for network normal response.
        viewModel.response.observe(this) { response ->
            if (response?.status.equals("OK", true)) {
                if (response?.results.isNullOrEmpty().not()) {
                    results.removeAll(results)
                    results.addAll(response?.results!!)
                    topHomeStoriesAdapter?.update(results)
                }
            } else {
                Toast.makeText(this, "Error getting top home stories", Toast.LENGTH_SHORT).show()
            }
            binding.progressCircular.visibility = View.GONE
        }

        // Initialize observer for erroneous network response.
        viewModel.errorResponse.observe(this) { errorResponse ->
            Toast.makeText(this, errorResponse.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun initializeUI() {
        topHomeStoriesAdapter = TopHomeItemsAdapter(results)
        binding.rvTopStoriesList.apply {
            adapter = topHomeStoriesAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        binding.btGetStories.setOnClickListener {
            binding.progressCircular.visibility = View.VISIBLE
            viewModel.getHomeTopStoriesOnline()
        }
    }
}