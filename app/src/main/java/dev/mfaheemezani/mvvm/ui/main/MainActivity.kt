package dev.mfaheemezani.mvvm.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import dev.mfaheemezani.mvvm.databinding.ActivityMainBinding
import dev.mfaheemezani.mvvm.ui.tophome.TopHomeStoriesActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var hasClicked = false // Flag to avoid multiple clicks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeUI()
    }

    override fun onResume() {
        super.onResume()
        hasClicked = false // Reset flag
    }

    private fun initializeUI() {
        val topHomeStoriesClickListener = View.OnClickListener {
            if (hasClicked.not()) {
                hasClicked = true
                startActivity(Intent(this, TopHomeStoriesActivity::class.java))
            }
        }
        binding.apply {
            llTopHomeStories.setOnClickListener(topHomeStoriesClickListener)
            tvTopHomeStories.setOnClickListener(topHomeStoriesClickListener)
            llInnerTopHomeStories.setOnClickListener(topHomeStoriesClickListener)
            ivTopHomeStories.setOnClickListener(topHomeStoriesClickListener)
        }
    }

}