package com.project.nizwar.storyapp.view.main

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.nizwar.storyapp.R
import com.project.nizwar.storyapp.data.Result
import com.project.nizwar.storyapp.data.model.Story
import com.project.nizwar.storyapp.databinding.ActivityMainBinding
import com.project.nizwar.storyapp.utils.ViewModelFactory
import com.project.nizwar.storyapp.view.detail.DetailActivity
import com.project.nizwar.storyapp.view.login.LoginActivity
import com.project.nizwar.storyapp.view.post.PostActivity
import com.project.nizwar.storyapp.view.settings.SettingsActivity


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainViewModel.getToken().observe(this) { token ->
            if (token != null) {
                mainViewModel.getAllStories(token).observe(this) {
                    when (it) {
                        is Result.Loading -> {
                            showLoading(true)
                        }
                        is Result.Success -> {
                            showLoading(false)
                            showRecyclerList(it.data.listStory)
                        }
                        is Result.Error -> {
                            showLoading(false)
                            Toast.makeText(this, getString(R.string.load_error), Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }

        binding.fabPost.setOnClickListener {
            val intent = Intent(this@MainActivity, PostActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showRecyclerList(listStory: List<Story>) {
        if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.rvStory.layoutManager = GridLayoutManager(this, 2)
        } else {
            binding.rvStory.layoutManager = LinearLayoutManager(this)
        }
        val listStoryAdapter = ListAdapter(listStory)

        listStoryAdapter.setOnItemClickCallBack(object : ListAdapter.OnItemClickCallback {
            override fun onItemClicked(story: Story, optionsCompat: ActivityOptionsCompat) {
                val intent = Intent(this@MainActivity, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_ID, story.id)
                startActivity(intent, optionsCompat.toBundle())
            }
        })

        binding.rvStory.adapter = listStoryAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                mainViewModel.clearToken()
                toLoginActivity()
                true
            }
            R.id.settings -> {
                toSettingsActivity()
                true
            }
            else -> {
                true
            }
        }
    }

    private fun toSettingsActivity() {
        val intent = Intent(this@MainActivity, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun toLoginActivity() {
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}