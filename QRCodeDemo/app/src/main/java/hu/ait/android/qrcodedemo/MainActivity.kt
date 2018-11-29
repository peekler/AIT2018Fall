package hu.ait.android.qrcodedemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.livinglifetechway.quickpermissions.annotations.WithPermissions
import kotlinx.android.synthetic.main.activity_main.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        startQRCodeReading()
    }

    override fun onPause() {
        super.onPause()
        zxingView.stopCamera()
    }

    @WithPermissions(
        permissions = [android.Manifest.permission.CAMERA]
    )
    fun startQRCodeReading() {
        zxingView.setResultHandler {
            tvScan.text = it.text

            if (it.text.endsWith(".jpg"))
            {
                Glide.with(this@MainActivity).load(it.text).into(ivImage)
            }

        }
        zxingView.startCamera()
    }

}
