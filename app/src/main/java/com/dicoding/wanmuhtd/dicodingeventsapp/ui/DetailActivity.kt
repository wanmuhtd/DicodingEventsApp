package com.dicoding.wanmuhtd.dicodingeventsapp.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.wanmuhtd.dicodingeventsapp.databinding.ActivityDetailBinding

class DetailActivity  : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    // Ambil eventId dari Intent
    private val eventId: Int by lazy {
        intent.getIntExtra(EXTRA_EVENT_ID, -1) // Ganti dengan ID default yang sesuai
    }

    // Inisialisasi DetailViewModel
    private val detailViewModel: DetailViewModel by lazy {
        ViewModelProvider(this, DetailViewModelFactory(eventId)).get(DetailViewModel::class.java)
    }

    companion object {
        const val EXTRA_EVENT_ID = "extra_event_id" // Ganti dari EXTRA_EVENT ke EXTRA_EVENT_ID
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Observe LiveData untuk memperbarui UI
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
        // Observasi perubahan data event
        detailViewModel.event.observe(this, Observer { event ->
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
        })

        // Observasi perubahan loading state
        detailViewModel.isLoading.observe(this, Observer { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        // Observasi pesan error
        detailViewModel.errorMessage.observe(this, Observer { eventWrapper ->
            eventWrapper.getContentIfNotHandled()?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupBackButton() {
        binding.btnBackContainer.setOnClickListener {
            @Suppress("DEPRECATION")
            onBackPressed()
        }
    }
}
/*
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private val event : ListEventsItem by lazy {
        getEventFromIntent()
    }

    companion object {
        const val EXTRA_EVENT = "extra_event"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tvEventName.text = event.name
        binding.tvDescription.text = event.summary
        binding.tvOwner.text = event.ownerName
        binding.tvBeginTime.text = event.beginTime

        Glide.with(this)
            .load(event.mediaCover)
            .into(binding.ivEventPicture)

        val quota = event.quota ?: 0
        val registrants = event.registrants ?: 0
        binding.tvRegistrantQuota.text = (quota - registrants).toString()

        binding.tvDescription.text = event.description?.let {
            HtmlCompat.fromHtml(
                it,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.link))
            startActivity(intent)
        }

        binding.btnBackContainer.setOnClickListener {
            @Suppress("DEPRECATION")
            onBackPressed()
        }
    }

    private fun getEventFromIntent() : ListEventsItem{
        val defaultEvent = ListEventsItem(
            summary = "No Summary Available",
            mediaCover = "default_image_url",
            registrants = 0,
            imageLogo = "default_logo_url",
            link = "https://example.com",
            description = "No Description Available",
            ownerName = "Unknown Owner",
            cityName = "Unknown City",
            quota = 0,
            name = "Unknown Event",
            id = -1, // Or some invalid ID
            beginTime = "N/A",
            endTime = "N/A",
            category = "Uncategorized"
        )

        return if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(EXTRA_EVENT, ListEventsItem::class.java) ?: defaultEvent
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_EVENT) ?: defaultEvent
        }
    }
}

 */