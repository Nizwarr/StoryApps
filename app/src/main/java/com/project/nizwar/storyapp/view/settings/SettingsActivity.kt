package com.project.nizwar.storyapp.view.settings

import android.app.LocaleManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.LocaleList
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.project.nizwar.storyapp.R
import com.project.nizwar.storyapp.databinding.ActivitySettingsBinding
import com.project.nizwar.storyapp.utils.ViewModelFactory

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val settingsViewModel by viewModels<SettingsViewModel> {
        ViewModelFactory.getInstance(application)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val language: Array<String> = resources.getStringArray(R.array.language)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, language)

        settingsViewModel.getLocaleSetting().observe(this) {
            when (it) {
                "en" -> {
                    binding.dropDown.setSelection(arrayAdapter.getPosition(language[0]))
                }
                "in" -> {
                    binding.dropDown.setSelection(arrayAdapter.getPosition(language[1]))
                }
            }
        }

        binding.dropDown.apply {
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (parent.getItemAtPosition(position).toString() == language[0]) {
                        setLocaleSettings("en")

                    } else {
                        setLocaleSettings("in")
                    }

                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
            adapter = arrayAdapter
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun setLocaleSettings(localeId: String) {
        settingsViewModel.saveLocaleSetting(localeId)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.getSystemService(LocaleManager::class.java).applicationLocales =
                LocaleList.forLanguageTags(localeId)
        } else {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(localeId))
        }
    }
}