package com.dicoding.wanmuhtd.dicodingeventsapp.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.wanmuhtd.dicodingeventsapp.databinding.ActivityDetailBinding

class DetailActivity  : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    private val eventId: Int by lazy {
        intent.getIntExtra(EXTRA_EVENT_ID, -1)
    }

    private val detailViewModel: DetailViewModel by lazy {
        ViewModelProvider(this, DetailViewModelFactory(eventId))[DetailViewModel::class.java]
    }

    companion object {
        const val EXTRA_EVENT_ID = "extra_event_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupBackButton()
    }

    private fun setupRegisterButton(link: String?) {
        binding.btnRegister.setOnClickListener {
            if (link.isNullOrEmpty()) {
                Toast.makeText(this, "Registration link is not available", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                startActivity(intent)
            }
        }
    }

    private fun setupObservers() {
        detailViewModel.event.observe(this) { event ->
            if (event != null) {
                binding.tvEventName.text = event.name
                binding.tvDescription.text = HtmlCompat.fromHtml(
                    event.description ?: "No Description Available",
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                binding.tvOwner.text = event.ownerName
                binding.tvBeginTime.text = event.beginTime

                Glide.with(this)
                    .load(event.mediaCover)
                    .into(binding.ivEventPicture)

                val quota = event.quota ?: 0
                val registrants = event.registrants ?: 0
                binding.tvRegistrantQuota.text = (quota - registrants).toString()
            }
            setupRegisterButton(event?.link)
        }

        detailViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        detailViewModel.errorMessage.observe(this) { eventWrapper ->
            eventWrapper.getContentIfNotHandled()?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupBackButton() {
        binding.btnBackContainer.setOnClickListener {
            @Suppress("DEPRECATION")
            onBackPressed()
        }
    }
}