package com.example.tt_bssd5250_111

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.VideoView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.GestureDetectorCompat
import kotlin.reflect.typeOf


private const val VIDEO_PATH1 = "video_path1"
private const val VIDEO_PATH2 = "video_path2"
private lateinit var Paths: ArrayList<String>

class VideoFragment : Fragment() {
    private var videoPath: String? = null
    private var videosMP: MediaPlayer? = null
    private lateinit var videoView: VideoView
    private lateinit var mDetector: GestureDetectorCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            var videoPath1 = it.getString(VIDEO_PATH1)
            var videoPath2 = it.getString(VIDEO_PATH2)
            Paths = arrayListOf(videoPath1.toString(), videoPath2.toString())
            videoPath = it.getString(VIDEO_PATH1)
        }
        mDetector = GestureDetectorCompat(requireContext(), VideoGestureListener())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v: View = inflater.inflate(R.layout.fragment_video, container, false)
        videoView = v.findViewById<VideoView>(R.id.myvideoView).apply {
            setVideoPath(videoPath)
            start()

            setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                    if (p1 != null) {
                        mDetector.onTouchEvent(p1)
                    }
                    return true
                }
            })
            setOnPreparedListener(object : MediaPlayer.OnPreparedListener {
                override fun onPrepared(p0: MediaPlayer?) {
                    videosMP = p0
                }
            })
        }
        return v
    }

    companion object {
        @JvmStatic
        fun newInstance(path: ArrayList<String>) =
            VideoFragment().apply {
                arguments = Bundle().apply {
                    putString(VIDEO_PATH1, path.get(0))
                    putString(VIDEO_PATH2, path.get(1))
                }
            }
    }

    private inner class VideoGestureListener : GestureDetector.SimpleOnGestureListener() {
        val context = requireContext()
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
        var currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()

        override fun onLongPress(e: MotionEvent) {
            if (Paths[0] == videoPath) {
                videoPath = Paths[1]
                startVideo(videoView, videoPath)
            } else {
                videoPath = Paths[0]
                startVideo(videoView, videoPath)
            }
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            Log.d("Double", "${e.x}")
            if (videoView.isPlaying) {
                videoView.pause()
            } else {
                videoView.start()
            }
            return true
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            var pos = videoView.currentPosition
            val amt = 10000
            val vol = 0.25F
            var newVolume = 0F
            var currentVolumePercentage = currentVolume / maxVolume
            Log.d("Fling", "Vx:$velocityX, Vy:$velocityY")
            newVolume = if (velocityY > -200) {
                if (currentVolumePercentage < vol) {
                    0F
                } else {
                    currentVolumePercentage - vol
                }
            } else {
                if (currentVolumePercentage == 1F) {
                    1F
                } else {
                    currentVolumePercentage + vol
                }
            }
            videosMP?.setVolume(newVolume, newVolume)
            currentVolume = newVolume * maxVolume
            if (velocityX < -500 && velocityX > -150){
                pos -= amt
                if (pos < 0) {
                    pos = 0
                }
            } else if (velocityX > 500) {
                pos += amt
            }
            videoView.seekTo(pos)
            return true
        }
    }

    private fun startVideo(videoView: VideoView, path: String?) {
        videoView.setVideoPath(path)
        videoView.start()
    }
}
