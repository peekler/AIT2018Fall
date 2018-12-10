package hu.aut.android.sensorchartdemo


import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_sensor_demo.*

class SensorDemoActivity : AppCompatActivity(), SensorEventListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor_demo)

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        btnListSensors.setOnClickListener {
            val sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL)
            tvSensors.text = ""
            for (sensor in sensorList) {
                tvSensors!!.append(sensor.name + "\n")
            }
        }

        btnStartSensor.setOnClickListener {
            val accelero = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            sensorManager.registerListener(
                this@SensorDemoActivity,
                accelero,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onDestroy() {
        (getSystemService(Context.SENSOR_SERVICE) as SensorManager).unregisterListener(this)
        super.onDestroy()
    }

    override fun onSensorChanged(event: SensorEvent) {
        tvValue.text = "X: " + event.values[0] + "\n" +
                "Y: " + event.values[1] + "\n" +
                "Z: " + event.values[2]
        //tvValue.setText("MAG: "+event.values[0]+"\n"+
        //                "ACC: "+event.accuracy);
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

    }
}
