package com.example.farmer.customer.ui.productsboard.productdetail

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.farmer.R
import com.example.farmer.customer.data.local.AppDatabase
import com.example.farmer.customer.data.network.RetrofitClientCustomer
import com.example.farmer.customer.data.network.model.Product
import com.example.farmer.customer.data.repository.ProductBasketRepository
import com.example.farmer.customer.data.repository.ProductRepository
import com.example.farmer.customer.ui.ViewModelFactory
import com.example.farmer.customer.ui.productsboard.productdetail.adapter.ImagesSliderAdapter
import com.example.farmer.databinding.FragmentProductDetailBinding
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationManager
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ProductDetailFragment : Fragment() {

    private val args: ProductDetailFragmentArgs by navArgs()

    private val viewModel: ViewModelProductDetail by viewModels {
        ViewModelFactory(
            mapOf(
                ViewModelProductDetail::class.java to {
                    ViewModelProductDetail(
                        requireActivity().application,
                        ProductRepository(
                            RetrofitClientCustomer.instance
                        ),
                        ProductBasketRepository(
                            RetrofitClientCustomer.instance,
                            AppDatabase.getDatabase(requireActivity().application).basketDao()
                        )
                    )
                }
            ))
    }

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var userLocationLayer: UserLocationLayer
    private lateinit var locationManager: LocationManager

    private val locationListener = object : com.yandex.mapkit.location.LocationListener {
        override fun onLocationUpdated(location: com.yandex.mapkit.location.Location) {
            val userPoint = location.position
            val product = args.product
            val farmPoint = Point(product.latitude, product.longitude)

            android.util.Log.d("MAP_DEBUG", "User: ${userPoint.latitude}, ${userPoint.longitude}")
            android.util.Log.d("MAP_DEBUG", "Farm: ${farmPoint.latitude}, ${farmPoint.longitude}")

            if (_binding != null) {
                drawStaticPoints(userPoint, farmPoint)
            }
        }

        override fun onLocationStatusUpdated(status: com.yandex.mapkit.location.LocationStatus) {
            android.util.Log.d("MAP_DEBUG", "Status: ${status.name}")
        }
    }

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
        //setupUserLocationLayer()
        //smartLocationFetch()

        val product = args.product
        setupStaticData(product)
        viewModelObserver()
        viewModel.loadImages(product.id)



        binding.btnToBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnShareMenu.setOnClickListener { view -> showSharePopupMenu(view, product) }

        setupMapLogic(farmPoint = Point(product.latitude, product.longitude))

        binding.btnAddToBasket.setOnClickListener {
            viewModel.addToBasket(product = product)
            Toast.makeText(
                requireContext(),
                "${product.name} добавлен в корзину",
                Toast.LENGTH_SHORT
            ).show()
        }


    }

    private fun setupMapLogic(farmPoint: Point) {
        locationManager = MapKitFactory.getInstance().createLocationManager()

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.requestSingleUpdate(locationListener)
        } else {
            drawOnlyFarmPoint(farmPoint)
            permissionRequest.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        }
    }

    private fun drawStaticPoints(userPoint: Point, farmPoint: Point) {
        val map = binding.mapviewCustomer.mapWindow.map
        val mapObjects = map.mapObjects
        mapObjects.clear()

        map.isScrollGesturesEnabled = false
        map.isZoomGesturesEnabled = false
        map.isRotateGesturesEnabled = false
        map.isTiltGesturesEnabled = false

        val userPin = mapObjects.addPlacemark().apply {
            geometry = userPoint
        }
        val farmPin = mapObjects.addPlacemark().apply {
            geometry = farmPoint
        }

        val boundingBox = BoundingBox(
            Point(
                minOf(userPoint.latitude, farmPoint.latitude),
                minOf(userPoint.longitude, farmPoint.longitude)
            ),
            Point(
                maxOf(userPoint.latitude, farmPoint.latitude),
                maxOf(userPoint.longitude, farmPoint.longitude)
            )
        )


        binding.mapviewCustomer.postDelayed({
            if (_binding != null) {
                val geometry = Geometry.fromBoundingBox(boundingBox)
                val calculatedPos = map.cameraPosition(geometry)

                val finalZoom = if (calculatedPos.zoom < 3.0f) 3.5f else calculatedPos.zoom - 0.8f

                android.util.Log.d(
                    "MAP_DEBUG",
                    "Moving to: ${calculatedPos.target.latitude}, zoom: $finalZoom"
                )

                map.move(
                    CameraPosition(
                        calculatedPos.target,
                        finalZoom,
                        0f,
                        0f
                    ),
                    Animation(Animation.Type.SMOOTH, 1f),
                    null
                )
            }
        }, 1000)
    }

    private fun drawOnlyFarmPoint(farmPoint: Point) {
        val map = binding.mapviewCustomer.mapWindow.map
        map.mapObjects.clear()

        // Тоже используем актуальный метод
        map.mapObjects.addPlacemark().apply {
            geometry = farmPoint
            setIcon(ImageProvider.fromResource(requireContext(), R.drawable.ic_nav_marker))
        }

        map.move(CameraPosition(farmPoint, 14f, 0f, 0f))

        map.isScrollGesturesEnabled = false
        map.isZoomGesturesEnabled = false
    }

    private fun showSharePopupMenu(anchor: View, product: Product) {
        val popup = PopupMenu(requireContext(), anchor)
        popup.menu.add(0, 1, 0, "Скопировать ссылку")

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    copyLinkToClipboard(product)
                    true
                }

                else -> false
            }
        }
        popup.show()
    }

    private fun copyLinkToClipboard(product: Product) {
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val shareText =
            "Посмотри, какой товар я нашел: ${product.name}\nhttps://farmerapp.com/product/${product.id}"
        val clip = ClipData.newPlainText("FarmerProductLink", shareText)
        clipboard.setPrimaryClip(clip)
    }

    private fun viewModelObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.images.collectLatest { imageUrls ->
                if (imageUrls.isNotEmpty()) {
                    binding.productViewPager.adapter = ImagesSliderAdapter(imageUrls)
                } else {
                    binding.productViewPager.adapter = ImagesSliderAdapter(listOf())
                }
            }
        }
    }

    private fun setupStaticData(product: Product) {
        binding.tvProductName.text = product.name
        binding.tvPrice.text = product.priceText
        binding.tvDescription.text = product.description
        binding.tvFarmName.text = product.farmName
        binding.tvPosName.text = product.posName
    }

//    private fun setupUserLocationLayer() {
//        val mapKit = MapKitFactory.getInstance()
//
//        // Создаем слой местоположения
//        userLocationLayer = mapKit.createUserLocationLayer(binding.mapviewCustomer.mapWindow)
//        userLocationLayer.isVisible = true
//
//        // Если хотите свою иконку вместо синей точки:
//        userLocationLayer.setObjectListener(object : UserLocationObjectListener {
//            override fun onObjectAdded(view: UserLocationView) {
//                smartLocationFetch()
//                // в будущем можно добавить свои иконки
//                view.pin.setIcon(ImageProvider.fromResource(context, R.drawable.ic_nav_marker))
//                view.arrow.setIcon(ImageProvider.fromResource(context, R.drawable.ic_nav_marker))
//            }
//
//            override fun onObjectRemoved(view: UserLocationView) {}
//            override fun onObjectUpdated(view: UserLocationView, event: ObjectEvent) {}
//        })
//    }

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
        binding.mapviewCustomer.mapWindow.map.move(
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
        binding.mapviewCustomer.onStart()
    }

    override fun onStop() {
        binding.mapviewCustomer.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}