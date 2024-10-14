package com.dicoding.wanmuhtd.dicodingeventsapp.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.dicoding.wanmuhtd.dicodingeventsapp.data.model.ListEventsItem
import com.dicoding.wanmuhtd.dicodingeventsapp.databinding.ActivityDetailBinding

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