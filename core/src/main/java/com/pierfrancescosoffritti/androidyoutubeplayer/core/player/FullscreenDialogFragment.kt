package com.pierfrancescosoffritti.androidyoutubeplayer.core.player

import android.app.Dialog
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.webkit.WebChromeClient
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import com.pierfrancescosoffritti.androidyoutubeplayer.R

class FullscreenDialogFragment : DialogFragment() {

    private var videoContainer: FrameLayout? = null
    private var videoView: View? = null
    private var player: YouTubePlayer? = null

    companion object {
        fun newInstance(view: View?, player: YouTubePlayer?): FullscreenDialogFragment {
            val instance = FullscreenDialogFragment()
            instance.videoView = view
            instance.player = player
            return instance
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnKeyListener { _dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                dismissDialog()
                return@setOnKeyListener true
            }
            false
        }

        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fullscreen_layout, container, false)
        videoContainer = view.findViewById(R.id.rootView)
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (videoContainer?.childCount ?: 0 > 0) {
            videoContainer?.removeAllViews()
        }

        // TODO: can not set MATCH_PARENT here.....need to be fix later
        val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        lp.gravity = Gravity.CENTER

        videoContainer?.addView(videoView, lp)
        player?.play()
    }

    override fun onStart() {
        super.onStart()
        dialog?.apply {
            setCancelable(true)
            window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.BLACK))
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
        }
    }

    private fun dismissDialog() {
        player?.exitFullscreen()
    }

}