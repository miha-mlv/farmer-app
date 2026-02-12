package com.example.farmer.customer.ui.productsboard.productdetail

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.farmer.R
import com.example.farmer.databinding.FragmentProductDetailBinding
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider


class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var userLocationLayer: UserLocationLayer

    private val permissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            smartLocationFetch()
        } else {
            Toast.makeText(context, "Доступ к GPS отклонен", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUserLocationLayer()
        smartLocationFetch()


    }

    private fun setupUserLocationLayer() {
        val mapKit = MapKitFactory.getInstance()

        // Создаем слой местоположения
        userLocationLayer = mapKit.createUserLocationLayer(binding.mapview.mapWindow)
        userLocationLayer.isVisible = true

        // Если хотите свою иконку вместо синей точки:
        userLocationLayer.setObjectListener(object : UserLocationObjectListener {
            override fun onObjectAdded(view: UserLocationView) {
                smartLocationFetch()
                // в будущем можно добавить свои иконки
                view.pin.setIcon(ImageProvider.fromResource(context, R.drawable.ic_nav_marker))
                view.arrow.setIcon(ImageProvider.fromResource(context, R.drawable.ic_nav_marker))
            }
            override fun onObjectRemoved(view: UserLocationView) {}
            override fun onObjectUpdated(view: UserLocationView, event: ObjectEvent) {}
        })
    }

    private fun smartLocationFetch() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {

            val locationManager = MapKitFactory.getInstance().createLocationManager()
            locationManager.requestSingleUpdate(object : LocationListener {
                override fun onLocationUpdated(location: Location) {
                    moveCameraToPoint(location.position)
                }

                override fun onLocationStatusUpdated(status: LocationStatus) {}
            })
        } else {
            permissionRequest.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        }
    }

    private fun moveCameraToPoint(point: Point) {
        binding.mapview.mapWindow.map.move(
            CameraPosition(point, 16f, 0f, 0f),
            Animation(Animation.Type.SMOOTH, 1.5f),
            null
        )
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProductDetailFragment().apply {}
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapview.onStart()
    }

    override fun onStop() {
        binding.mapview.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}