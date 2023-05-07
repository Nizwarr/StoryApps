package com.project.nizwar.storyapp.view.post

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.project.nizwar.storyapp.R
import com.project.nizwar.storyapp.data.Result
import com.project.nizwar.storyapp.utils.createTempFile
import com.project.nizwar.storyapp.databinding.ActivityPostBinding
import com.project.nizwar.storyapp.utils.ViewModelFactory
import com.project.nizwar.storyapp.utils.reduceFileImage
import com.project.nizwar.storyapp.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostBinding

    private val postViewModel by viewModels<PostViewModel> {
        ViewModelFactory.getInstance(application)
    }

    private lateinit var currentPhotoPath: String
    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = resources.getString(R.string.post_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        binding.apply {
            buttonCamera.setOnClickListener { startTakePhoto() }
            buttonGallery.setOnClickListener { startGallery() }
            postViewModel.getToken().observe(this@PostActivity) { token ->
                buttonAdd.setOnClickListener { postStory(token!!) }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun postStory(token: String) {
        showLoading(true)
        val description = binding.edAddDescription.text.toString()

        if (getFile == null) {
            Toast.makeText(
                this@PostActivity,
                getString(R.string.no_picture),
                Toast.LENGTH_SHORT
            ).show()
            showLoading(false)
        } else if (description.isBlank()) {
            Toast.makeText(
                this@PostActivity,
                getString(R.string.no_desc),
                Toast.LENGTH_SHORT
            ).show()
            showLoading(false)
        } else if (token != null) {
            val file = reduceFileImage(getFile as File)
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            postViewModel.addNewStory(token, imageMultipart, description).observe(this) {
                when (it) {
                    is Result.Loading -> {
                        showLoading(true)
                        Toast.makeText(
                            this@PostActivity,
                            getString(R.string.loading),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is Result.Success -> {
                        showLoading(false)
                        Toast.makeText(
                            this@PostActivity,
                            getString(R.string.post_succed),
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                    is Result.Error -> {
                        showLoading(false)
                        Toast.makeText(
                            this@PostActivity,
                            getString(R.string.post_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@PostActivity,
                "com.project.nizwar.storyapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            val result = BitmapFactory.decodeFile(myFile.path)
            getFile = myFile
            binding.ivPreview.setImageBitmap(result)
        }
    }
    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@PostActivity)
            getFile = myFile
            binding.ivPreview.setImageURI(selectedImg)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}