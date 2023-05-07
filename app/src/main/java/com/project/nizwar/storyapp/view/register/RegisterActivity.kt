package com.project.nizwar.storyapp.view.register

import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.project.nizwar.storyapp.R
import com.project.nizwar.storyapp.data.Result
import com.project.nizwar.storyapp.databinding.ActivityRegisterBinding
import com.project.nizwar.storyapp.utils.ViewModelFactory

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private val registerViewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = resources.getString(R.string.title_register)

        binding.apply {
            btnRegister.setOnClickListener {
                val name = edRegisterName.text.toString().trim()
                val email = edRegisterEmail.text.toString().trim()
                val password = edRegisterPassword.text.toString().trim()

                if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                    if (Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length >= 8) {
                        register(name, email, password)
                    } else {
                        Toast.makeText(
                            this@RegisterActivity, getString(R.string.invalid_data), Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@RegisterActivity, getString(R.string.enter_data), Toast.LENGTH_SHORT
                    ).show()
                }
            }
            tvLogin.setOnClickListener { finish() }
        }

        playAnimation()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }

    private fun register(name: String, email: String, password: String) {
        binding.apply {
            registerViewModel.register(name, email, password).observe(this@RegisterActivity) {
                when (it) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        finish()
                    }
                    is Result.Error -> {
                        showLoading(false)
                        Toast.makeText(
                            this@RegisterActivity,
                            getString(R.string.register_failed),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }

        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}