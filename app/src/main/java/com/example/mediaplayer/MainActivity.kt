package com.example.mediaplayer

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DefaultDataSourceFactory
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ConcatenatingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.example.mediaplayer.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getAllRawResources()
    }

    @OptIn(UnstableApi::class)
    @Suppress("DEPRECATION")
    private fun getAllRawResources() {
        val rawResources = listOf(R.raw.cococola, R.raw.tulip, R.raw.clock)

        val concatenatingMediaSource = ConcatenatingMediaSource()

        for (videoResource in rawResources) {
            val uri = RawResourceDataSource.buildRawResourceUri(videoResource)
            val mediaItem = MediaItem.fromUri(uri)
            concatenatingMediaSource.addMediaSource(
                ProgressiveMediaSource.Factory(DefaultDataSourceFactory(this, Util.getUserAgent(this, "24SJU"))).createMediaSource(mediaItem)
            )
        }

        playConcatenatedMediaSource(concatenatingMediaSource)
    }


    @OptIn(UnstableApi::class)
    private fun playConcatenatedMediaSource(concatenatedSource: ConcatenatingMediaSource) {
        try {
            if (player == null) {
                player = ExoPlayer.Builder(this).build()
                binding.mediaPlayer.player = player
                Log.e("video", "ExoPlayer initialized.")
                Toast.makeText(this, "ExoPlayer Initialized", Toast.LENGTH_SHORT).show()
            }

            player?.setMediaSource(concatenatedSource)
            player?.prepare()
            player?.playWhenReady = true
            player?.repeatMode = Player.REPEAT_MODE_ALL

            player?.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_ENDED -> {
                            Log.e("video", "Playback ended.")
                            Toast.makeText(this@MainActivity, "Playback Ended", Toast.LENGTH_SHORT).show()
                            handleVideoEnd()
                        }
                        else -> {
                            Log.w("video", "Playback ended.")
                        }
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    Log.e("video", "Error playing video: ${error.message}")
                    Toast.makeText(this@MainActivity, "Error playing video: ${error.message}", Toast.LENGTH_LONG).show()
                }
            })
        } catch (e: Exception) {
            Log.e("video", "Error preparing or playing video: ${e.message}")
            Toast.makeText(this, "Error preparing or playing video: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun handleVideoEnd() {
        Log.e("video", "Handling video end, replaying raw resources.")
        Toast.makeText(this, "Handling video end, replaying raw resources.", Toast.LENGTH_SHORT).show()
        getAllRawResources()
    }
}
