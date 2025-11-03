package com.example.p_music.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.p_music.MainActivity
import com.example.p_music.R
import com.example.p_music.domain.service.AudioPlayerService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AudioPlaybackService : Service() {

    @Inject
    lateinit var audioPlayerService: AudioPlayerService

    private val binder = LocalBinder()
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var notificationJob: Job? = null
    private var progressUpdateJob: Job? = null
    private var lastNotificationUpdate = 0L
    private val NOTIFICATION_UPDATE_INTERVAL = 2000L // 2 secondes

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "audio_playback_channel"
        const val ACTION_PLAY_PAUSE = "com.example.p_music.ACTION_PLAY_PAUSE"
        const val ACTION_NEXT = "com.example.p_music.ACTION_NEXT"
        const val ACTION_PREVIOUS = "com.example.p_music.ACTION_PREVIOUS"
        const val ACTION_STOP = "com.example.p_music.ACTION_STOP"
    }

    inner class LocalBinder : Binder() {
        fun getService(): AudioPlaybackService = this@AudioPlaybackService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        observePlaybackState()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY_PAUSE -> audioPlayerService.togglePlayPause()
            ACTION_NEXT -> audioPlayerService.playNext()
            ACTION_PREVIOUS -> audioPlayerService.playPrevious()
            ACTION_STOP -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun observePlaybackState() {
        notificationJob = serviceScope.launch {
            audioPlayerService.currentAudio.collectLatest { audio ->
                if (audio != null) {
                    updateNotification()
                    startForeground(NOTIFICATION_ID, createNotification())
                }
            }
        }

        // Observer les changements de lecture (mise à jour immédiate)
        serviceScope.launch {
            audioPlayerService.isPlaying.collectLatest {
                updateNotification(forceUpdate = true)
            }
        }

        // Mettre à jour la progression périodiquement
        startProgressUpdates()
    }

    private fun startProgressUpdates() {
        progressUpdateJob?.cancel()
        progressUpdateJob = serviceScope.launch {
            while (true) {
                if (audioPlayerService.isPlaying.value) {
                    updateNotification(forceUpdate = false)
                }
                delay(NOTIFICATION_UPDATE_INTERVAL)
            }
        }
    }

    private fun updateNotification(forceUpdate: Boolean = false) {
        if (audioPlayerService.currentAudio.value != null) {
            val currentTime = System.currentTimeMillis()
            if (forceUpdate || currentTime - lastNotificationUpdate >= NOTIFICATION_UPDATE_INTERVAL) {
                val notification = createNotification()
                val notificationManager = getSystemService(NotificationManager::class.java)
                notificationManager.notify(NOTIFICATION_ID, notification)
                lastNotificationUpdate = currentTime
            }
        }
    }

    private fun createNotification(): Notification {
        val currentAudio = audioPlayerService.currentAudio.value ?: return createEmptyNotification()
        val title = currentAudio.title
        val artist = currentAudio.artist
        val duration = currentAudio.duration
        val elapsedTime = audioPlayerService.elapsedTime.value
        val progress = audioPlayerService.currentProgress.value
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val playPauseIntent = Intent(this, AudioPlaybackService::class.java).apply {
            action = ACTION_PLAY_PAUSE
        }
        val playPausePendingIntent = PendingIntent.getService(
            this,
            0,
            playPauseIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val previousIntent = Intent(this, AudioPlaybackService::class.java).apply {
            action = ACTION_PREVIOUS
        }
        val previousPendingIntent = PendingIntent.getService(
            this,
            1,
            previousIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val nextIntent = Intent(this, AudioPlaybackService::class.java).apply {
            action = ACTION_NEXT
        }
        val nextPendingIntent = PendingIntent.getService(
            this,
            2,
            nextIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val isPlaying = audioPlayerService.isPlaying.value
        val playPauseIcon = if (isPlaying) {
            android.R.drawable.ic_media_pause
        } else {
            android.R.drawable.ic_media_play
        }

        // Formater le temps écoulé et restant
        val elapsedTimeStr = formatTime(elapsedTime)
        val durationStr = formatTime(duration)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText("$artist • $elapsedTimeStr / $durationStr")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setLargeIcon(currentAudio.coverUri?.let { 
                try {
                    android.graphics.BitmapFactory.decodeStream(
                        contentResolver.openInputStream(it)
                    )
                } catch (e: Exception) { null }
            })
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setShowWhen(false)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setProgress(10000, (progress * 10000).toInt(), false)
            .addAction(android.R.drawable.ic_media_previous, "Précédent", previousPendingIntent)
            .addAction(playPauseIcon, if (isPlaying) "Pause" else "Lecture", playPausePendingIntent)
            .addAction(android.R.drawable.ic_media_next, "Suivant", nextPendingIntent)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
            .build()
    }

    private fun createEmptyNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("P-Music")
            .setContentText("Aucune musique en cours")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .build()
    }

    private fun formatTime(milliseconds: Long): String {
        val seconds = (milliseconds / 1000).toInt()
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%d:%02d", minutes, remainingSeconds)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Lecture Audio",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Contrôles de lecture audio avec progression"
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                enableVibration(false)
                setSound(null, null)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationJob?.cancel()
        progressUpdateJob?.cancel()
        serviceScope.cancel()
    }
}
