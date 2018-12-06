package hu.ait.android.musicplayerdemo

import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MediaPlayer.OnPreparedListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnDemo.setOnClickListener {
            var mediaPlayer = MediaPlayer.create(
                this@MainActivity, R.raw.demo
            )

            mediaPlayer.setOnPreparedListener(this)

            mediaPlayer.seekTo(60000)

        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mp?.start()
    }


}
