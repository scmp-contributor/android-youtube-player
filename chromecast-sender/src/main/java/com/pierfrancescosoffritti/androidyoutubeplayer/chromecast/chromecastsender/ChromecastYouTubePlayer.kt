package com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender

import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.io.infrastructure.ChromecastCommunicationChannel
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.io.youtube.ChromecastCommunicationConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.io.youtube.ChromecastYouTubeMessageDispatcher
import com.pierfrancescosoffritti.androidyoutubeplayer.chromecast.chromecastsender.utils.JSONUtils
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayerBridge
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener

class ChromecastYouTubePlayer internal constructor(private val chromecastCommunicationChannel: ChromecastCommunicationChannel) : YouTubePlayer, YouTubePlayerBridge.YouTubePlayerBridgeCallbacks {

    private lateinit var youTubePlayerInitListener: (YouTubePlayer) -> Unit

    private val inputMessageDispatcher = ChromecastYouTubeMessageDispatcher(YouTubePlayerBridge(this))
    private val youTubePlayerListeners = HashSet<YouTubePlayerListener>()

    internal fun initialize(initListener: (YouTubePlayer) -> Unit) {
        youTubePlayerListeners.clear()

        youTubePlayerInitListener = initListener

        chromecastCommunicationChannel.addObserver(inputMessageDispatcher)
    }

    override fun onYouTubeIFrameAPIReady() {
        youTubePlayerInitListener(this)
    }

    override fun onSmartEmbedNoVideo() {
        // notify no Smart Embed video when player ready
        for (listener in youTubePlayerListeners)
            listener.onSmartEmbedNoVideo(this)
    }

    override fun onReceiveVideoData(videoData: String, duration: Float) {
        TODO("Not yet implemented")
    }

    override fun getInstance(): YouTubePlayer {
        return this
    }

    override fun loadVideo(videoId: String, startSeconds: Float) {
        val message = JSONUtils.buildFlatJson(
                "command" to ChromecastCommunicationConstants.LOAD,
                "videoId" to videoId,
                "startSeconds" to startSeconds.toString()
        )

        chromecastCommunicationChannel.sendMessage(message)
    }

    override fun cueVideo(videoId: String, startSeconds: Float) {
        val message = JSONUtils.buildFlatJson(
                "command" to ChromecastCommunicationConstants.CUE,
                "videoId" to videoId,
                "startSeconds" to startSeconds.toString()
        )

        chromecastCommunicationChannel.sendMessage(message)
    }

    override fun play() {
        val message = JSONUtils.buildFlatJson(
                "command" to ChromecastCommunicationConstants.PLAY
        )

        chromecastCommunicationChannel.sendMessage(message)
    }

    override fun pause() {
        val message = JSONUtils.buildFlatJson(
                "command" to ChromecastCommunicationConstants.PAUSE
        )

        chromecastCommunicationChannel.sendMessage(message)
    }
    override fun mute() {
        val message = JSONUtils.buildFlatJson(
                "command" to ChromecastCommunicationConstants.MUTE
        )

        chromecastCommunicationChannel.sendMessage(message)
    }

    override fun unMute() {
        val message = JSONUtils.buildFlatJson(
                "command" to ChromecastCommunicationConstants.UNMUTE
        )

        chromecastCommunicationChannel.sendMessage(message)
    }

    override fun setVolume(volumePercent: Int) {
        val message = JSONUtils.buildFlatJson(
                "command" to ChromecastCommunicationConstants.SET_VOLUME,
                "volumePercent" to volumePercent.toString()
        )

        chromecastCommunicationChannel.sendMessage(message)
    }

    override fun seekTo(time: Float) {
        val message = JSONUtils.buildFlatJson(
                "command" to ChromecastCommunicationConstants.SEEK_TO,
                "time" to time.toString()
        )

        chromecastCommunicationChannel.sendMessage(message)
    }

    override fun addListener(listener: YouTubePlayerListener): Boolean = youTubePlayerListeners.add(listener)
    override fun removeListener(listener: YouTubePlayerListener): Boolean = youTubePlayerListeners.remove(listener)
    override fun fullscreen() {
        TODO("Not yet implemented")
    }

    override fun exitFullscreen() {
        TODO("Not yet implemented")
    }

    override fun videoID(): String? {
        TODO("Not yet implemented")
    }

    override fun author(): String? {
        TODO("Not yet implemented")
    }

    override fun title(): String? {
        TODO("Not yet implemented")
    }

    override fun duration(): Float {
        TODO("Not yet implemented")
    }

    override fun getListeners(): MutableCollection<YouTubePlayerListener> = youTubePlayerListeners
}