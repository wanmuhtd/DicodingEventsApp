package com.dicoding.wanmuhtd.dicodingeventsapp.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.wanmuhtd.dicodingeventsapp.R
import com.dicoding.wanmuhtd.dicodingeventsapp.data.local.entity.FavoriteEventsEntity
import com.dicoding.wanmuhtd.dicodingeventsapp.data.local.entity.PastEventsEntity
import com.dicoding.wanmuhtd.dicodingeventsapp.data.local.entity.UpcomingEventsEntity
import com.dicoding.wanmuhtd.dicodingeventsapp.databinding.ActivityDetailBinding
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.ViewModelFactory
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.active.UpcomingEventViewModel
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.favorite.FavoriteEventViewModel
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.past.PastEventViewModel
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.setting.SettingPreferences
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.setting.dataStore

class DetailActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityDetailBinding

    @Suppress("unused")
    private val detailEventViewModel: DetailViewModel by viewModels<DetailViewModel>()
    private val pref = SettingPreferences.getInstance(this.dataStore)
    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this, pref)
    private val upcomingEventViewModel: UpcomingEventViewModel by viewModels<UpcomingEventViewModel> { factory }
    private val pastEventViewModel: PastEventViewModel by viewModels<PastEventViewModel> { factory }
    private val favoriteEventViewModel: FavoriteEventViewModel by viewModels<FavoriteEventViewModel> { factory }

    private lateinit var upcomingEventsEntity: UpcomingEventsEntity
    private lateinit var pastEventsEntity: PastEventsEntity

    private val eventId: Int by lazy {
        intent.getIntExtra(EXTRA_EVENT_ID, -1)
    }

    private val eventStatus: Boolean by lazy {
        intent.getBooleanExtra(EXTRA_EVENT_STATUS, false)
    }

    private val detailViewModel: DetailViewModel by lazy {
        ViewModelProvider(
            this,
            DetailViewModelFactory(eventId, eventStatus)
        )[DetailViewModel::class.java]
    }

    companion object {
        const val EXTRA_EVENT_ID = "extra_event_id"
        const val EXTRA_EVENT_STATUS = "extra_event_status"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idEvent = intent.getIntExtra(EXTRA_EVENT_ID, -1)
        val statusEvent = intent.getBooleanExtra(EXTRA_EVENT_STATUS, false)

        setupObservers()
        setupBackButton()


        val icFavorite = binding.btnFavorite
        if (statusEvent) {
            upcomingEventViewModel.checkIsFavorite(idEvent).observe(this) { data ->
                upcomingEventsEntity = data
                if (data.isFavorite == true) {
                    icFavorite.setImageResource(R.drawable.ic_love_solid)
                } else {
                    icFavorite.setImageResource(R.drawable.ic_love_outline)
                }
            }
        } else {
            pastEventViewModel.checkIsFavorite(idEvent).observe(this) { data ->
                pastEventsEntity = data
                if (data.isFavorite == true) {
                    icFavorite.setImageResource(R.drawable.ic_love_solid)
                } else {
                    icFavorite.setImageResource(R.drawable.ic_love_outline)
                }
            }
        }

        binding.btnFavorite.setOnClickListener(this)
    }

    private fun setupRegisterButton(link: String?) {
        binding.btnRegister.setOnClickListener {
            if (link.isNullOrEmpty()) {
                Toast.makeText(this, "Registration link is not available", Toast.LENGTH_SHORT)
                    .show()
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_favorite -> {
                val idEvent = intent.getIntExtra(EXTRA_EVENT_ID, -1)
                val statusEvent = intent.getBooleanExtra(EXTRA_EVENT_STATUS, false)
                if (statusEvent) {
                    if (upcomingEventsEntity.isFavorite == true) {
                        upcomingEventViewModel.deleteEvent(idEvent)
                        favoriteEventViewModel.deleteFavoriteEvent(idEvent)
                    } else {
                        upcomingEventViewModel.saveEvent(idEvent)
                        favoriteEventViewModel.insertFavoriteEvent(
                            FavoriteEventsEntity(
                                upcomingEventsEntity.id,
                                upcomingEventsEntity.summary,
                                upcomingEventsEntity.mediaCover,
                                upcomingEventsEntity.registrants,
                                upcomingEventsEntity.imageLogo,
                                upcomingEventsEntity.link,
                                upcomingEventsEntity.description,
                                upcomingEventsEntity.ownerName,
                                upcomingEventsEntity.cityName,
                                upcomingEventsEntity.quota,
                                upcomingEventsEntity.name,
                                upcomingEventsEntity.beginTime,
                                upcomingEventsEntity.endTime,
                                upcomingEventsEntity.category,
                                statusEvent = true
                            )
                        )
                        Toast.makeText(
                            this,
                            "Berhasil menambahkan ke favorite!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    if (pastEventsEntity.isFavorite == true) {
                        pastEventViewModel.deleteEvent(idEvent)
                        favoriteEventViewModel.deleteFavoriteEvent(idEvent)
                    } else {
                        pastEventViewModel.saveEvent(idEvent)
                        favoriteEventViewModel.insertFavoriteEvent(
                            FavoriteEventsEntity(
                                pastEventsEntity.id,
                                pastEventsEntity.summary,
                                pastEventsEntity.mediaCover,
                                pastEventsEntity.registrants,
                                pastEventsEntity.imageLogo,
                                pastEventsEntity.link,
                                pastEventsEntity.description,
                                pastEventsEntity.ownerName,
                                pastEventsEntity.cityName,
                                pastEventsEntity.quota,
                                pastEventsEntity.name,
                                pastEventsEntity.beginTime,
                                pastEventsEntity.endTime,
                                pastEventsEntity.category,
                                statusEvent = false
                            )
                        )
                        Toast.makeText(
                            this,
                            "Berhasil menambahkan ke favorite!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}