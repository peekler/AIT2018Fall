package hu.ait.android.aitmapdemo

import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.livinglifetechway.quickpermissions.annotations.WithPermissions
import kotlinx.android.synthetic.main.activity_maps.*
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    MyLocationProvider.OnNewLocationAvailable {


    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btnNormal.setOnClickListener {
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

            mMap.clear()
        }
        btnSatellite.setOnClickListener {
            mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE

            val cameraPosition = CameraPosition.Builder()
                .target(LatLng(47.0, 19.0))
                .zoom(17f)
                .bearing(90f)
                .tilt(30f)
                .build()
            mMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    cameraPosition
                )
            )


        }
    }

    private lateinit var myLocationProvider: MyLocationProvider

    override fun onStart() {
        super.onStart()
        startLocation()
    }

    @WithPermissions(
        permissions = [android.Manifest.permission.ACCESS_FINE_LOCATION]
    )
    fun startLocation() {
        myLocationProvider = MyLocationProvider(
            this,
            this
        )
        myLocationProvider.startLocationMonitoring()
    }


    override fun onStop() {
        super.onStop()
        myLocationProvider.stopLocationMonitoring()
    }

    var prevPosition: Location? = null

    override fun onNewLocation(location: Location) {
        /*tvData.text =
                "Loc: ${location.latitude}, ${location.longitude}"

        /*if (location.provider == LocationManager.NETWORK_PROVIDER) {

            tvData.append(location.accuracy)
        }*/

        var dist = prevPosition?.distanceTo(location)
        Toast.makeText(this, "Distance: $dist (m)", Toast.LENGTH_LONG).show()
        prevPosition = location

        val markerOpt = MarkerOptions().
            position(LatLng(location.latitude, location.longitude)).
            title("My position")
        mMap.addMarker(markerOpt)*/
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val marker = LatLng(47.0, 19.0)
        mMap.addMarker(MarkerOptions().position(marker).title("Marker in Hungary"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker))

        mMap.isTrafficEnabled = true
        mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE


        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true


        mMap.setOnMapClickListener {
            val markerOpt = MarkerOptions().position(it).title("My marker ${it.latitude}, ${it.longitude}")
            val marker = mMap.addMarker(markerOpt)
            marker.isDraggable = true


            mMap.animateCamera(CameraUpdateFactory.newLatLng(it))
        }


        mMap.setOnMarkerClickListener {
            runOnUiThread() {
                val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())

                var addrs: List<Address>? = geocoder.getFromLocation(
                    it.position.latitude, it.position.longitude, 3
                )

                val addr = addrs?.get(0)?.getAddressLine(0) +
                        ",\n" + addrs?.get(0)?.getAddressLine(1) +
                        ",\n" + addrs?.get(0)?.getAddressLine(2) +
                        ",\n" + addrs?.get(0)?.getAddressLine(3)

                Toast.makeText(
                    this@MapsActivity, addr,
                    Toast.LENGTH_LONG
                ).show()
            }

            true
        }


        val polyRect: PolygonOptions = PolygonOptions().add(
            LatLng(24.0, 69.0),
            LatLng(24.0, 66.0),
            LatLng(28.0, 56.0),
            LatLng(28.0, 59.0)
        )
        val polygon: Polygon = mMap.addPolygon(polyRect)
        polygon.fillColor = Color.GREEN


    }
}
