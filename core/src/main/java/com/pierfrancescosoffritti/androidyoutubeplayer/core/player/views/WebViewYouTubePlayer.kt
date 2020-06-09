package com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewParent
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.pierfrancescosoffritti.androidyoutubeplayer.R
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.FullscreenDialogFragment
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.VideoConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayerBridge
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.Utils
import org.json.JSONObject
import java.util.*

/**
 * WebView implementation of [YouTubePlayer]. The player runs inside the WebView, using the IFrame Player API.
 */
internal class WebViewYouTubePlayer constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : WebView(context, attrs, defStyleAttr), YouTubePlayer, YouTubePlayerBridge.YouTubePlayerBridgeCallbacks {

    private lateinit var youTubePlayerInitListener: (YouTubePlayer) -> Unit

    private val youTubePlayerListeners = HashSet<YouTubePlayerListener>()
    private val mainThreadHandler: Handler = Handler(Looper.getMainLooper())

    private var videoData: JSONObject? = null
    private var duration = 0f
    internal var isBackgroundPlaybackEnabled = false

    private var scrollEnable = true

    internal fun initialize(initListener: (YouTubePlayer) -> Unit, playerOptions: IFramePlayerOptions?, isSmartEmbed: Boolean, channels: Array<String>?) {
        youTubePlayerInitListener = initListener
        initWebView(playerOptions ?: IFramePlayerOptions.default, isSmartEmbed, channels)
    }

    override fun onYouTubeIFrameAPIReady() = youTubePlayerInitListener(this)

    override fun onSmartEmbedNoVideo() {
        // notify no Smart Embed video when player ready
        mainThreadHandler.post {
            for (listener in youTubePlayerListeners)
                listener.onSmartEmbedNoVideo(this)
        }
    }

    override fun onReceiveVideoData(videoData: String, duration: Float) {
        this.videoData = JSONObject(videoData)
        this.duration = duration
    }

    override fun getInstance(): YouTubePlayer = this

    override fun loadVideo(videoId: String, startSeconds: Float) {
        mainThreadHandler.post { loadUrl("javascript:loadVideo('$videoId', $startSeconds)") }
    }

    override fun cueVideo(videoId: String, startSeconds: Float) {
        mainThreadHandler.post { loadUrl("javascript:cueVideo('$videoId', $startSeconds)") }
    }

    override fun play() {
        mainThreadHandler.post { loadUrl("javascript:playVideo()") }
    }

    override fun pause() {
        mainThreadHandler.post { loadUrl("javascript:pauseVideo()") }
    }

    override fun mute() {
        mainThreadHandler.post { loadUrl("javascript:mute()") }
    }

    override fun unMute() {
        mainThreadHandler.post { loadUrl("javascript:unMute()") }
    }

    override fun setVolume(volumePercent: Int) {
        require(!(volumePercent < 0 || volumePercent > 100)) { "Volume must be between 0 and 100" }

        mainThreadHandler.post { loadUrl("javascript:setVolume($volumePercent)") }
    }

    override fun seekTo(time: Float) {
        mainThreadHandler.post { loadUrl("javascript:seekTo($time)") }
    }

    override fun fullscreen() {
        mainThreadHandler.post { loadUrl("javascript:fullscreen()") }
    }

    override fun exitFullscreen() {
        mainThreadHandler.post { loadUrl("javascript:exitFullscreen()") }
    }

    override fun videoID() = videoData?.getString(VideoConstants.VIDEO_ID)
    override fun author() = videoData?.getString(VideoConstants.AUTHOR)
    override fun title() = videoData?.getString(VideoConstants.TITLE)
    override fun duration() = duration

    override fun destroy() {
        clear()
        super.destroy()
    }

    override fun getListeners(): Collection<YouTubePlayerListener> {
        return Collections.unmodifiableCollection(HashSet(youTubePlayerListeners))
    }

    override fun addListener(listener: YouTubePlayerListener): Boolean {
        return youTubePlayerListeners.add(listener)
    }

    override fun removeListener(listener: YouTubePlayerListener): Boolean {
        return youTubePlayerListeners.remove(listener)
    }

    private fun clear() {
        youTubePlayerListeners.clear()
        mainThreadHandler.removeCallbacksAndMessages(null)
    }

    fun beforeDestroy() {
        for (listener in youTubePlayerListeners) {
            listener.onYouTubePlayerDestroy()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView(playerOptions: IFramePlayerOptions, isSmartEmbed: Boolean, channels: Array<String>?) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // TODO ONLY for dev
            setWebContentsDebuggingEnabled(true);
        }

        settings.javaScriptEnabled = true
        settings.mediaPlaybackRequiresUserGesture = false
        settings.cacheMode = WebSettings.LOAD_NO_CACHE

        addJavascriptInterface(YouTubePlayerBridge(this), "YouTubePlayerBridge")
        loadHtmlPlayer(playerOptions, isSmartEmbed, channels)
    }

    fun reloadWebView(initListener: (YouTubePlayer) -> Unit, playerOptions: IFramePlayerOptions?, isSmartEmbed: Boolean, channels: Array<String>?) {

        clear()
        youTubePlayerInitListener  = initListener
        loadHtmlPlayer(playerOptions ?: IFramePlayerOptions.default, isSmartEmbed, channels)
    }

    private fun loadHtmlPlayer(playerOptions: IFramePlayerOptions, isSmartEmbed: Boolean, channels: Array<String>?) {

        val fileRes = if(!isSmartEmbed) R.raw.ayp_youtube_player else R.raw.ayp_smart_embed_youtube_player
        var htmlPage = Utils
                .readHTMLFromUTF8File(resources.openRawResource(fileRes))
                .replace("<<injectedPlayerVars>>", playerOptions.toString())

        if(isSmartEmbed && channels?.isNotEmpty() == true) {
            htmlPage = htmlPage.replace("<<channels>>", channels.joinToString(","))
        }

        loadDataWithBaseURL(playerOptions.getOrigin(), htmlPage, "text/html", "utf-8", null)

        // if the video's thumbnail is not in memory, show a black screen
        webChromeClient = object : WebChromeClient() {

            var playerView: View? = null
            var dialog: FullscreenDialogFragment? = null

            override fun getDefaultVideoPoster(): Bitmap? {
                val result = super.getDefaultVideoPoster()
                return result ?: Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565)
            }

            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                playerView = view
                dialog = FullscreenDialogFragment.newInstance(playerView, this@WebViewYouTubePlayer)
                dialog?.show((this@WebViewYouTubePlayer.context as AppCompatActivity).supportFragmentManager, "fullscreen")

                mainThreadHandler.post {
                    for (listener in youTubePlayerListeners)
                        listener.onYouTubePlayerEnterFullScreen(this@WebViewYouTubePlayer)
                }
            }

            override fun onHideCustomView() {
                dialog?.dismiss()
                dialog = null
                playerView = null

                mainThreadHandler.post {
                    for (listener in youTubePlayerListeners)
                        listener.onYouTubePlayerExitFullScreen(this@WebViewYouTubePlayer)
                }
            }
        }
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        if (isBackgroundPlaybackEnabled && (visibility == View.GONE || visibility == View.INVISIBLE))
            return

        super.onWindowVisibilityChanged(visibility)
    }

    override fun onOverScrolled(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY)
        val parentView = findScrollableParent() ?: return

        if (clampedY && scrollEnable) {
            parentView.requestDisallowInterceptTouchEvent(false)
        } else if (clampedX && scrollEnable) {
            parentView.requestDisallowInterceptTouchEvent(false)
        } else {
            scrollEnable = false
        }
    }

    override fun dispatchTouchEvent(motionEvent: MotionEvent?): Boolean {
        val parentView = findScrollableParent() ?: return super.dispatchTouchEvent(motionEvent)
        when (motionEvent?.action) {
            MotionEvent.ACTION_UP -> parentView.requestDisallowInterceptTouchEvent(false)
            MotionEvent.ACTION_DOWN -> {
                parentView.requestDisallowInterceptTouchEvent(true)
                scrollEnable = true
            }
        }

        return super.dispatchTouchEvent(motionEvent)
    }

    private fun findScrollableParent(): ViewParent? {
        var parentView = this.parent
        while (parentView != null && parentView != rootView) {
            when (parentView) {
                is ViewPager,
                is RecyclerView,
                is ScrollView -> return parentView
            }
            parentView = parentView.parent
        }
        return null
    }
}
