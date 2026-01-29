package com.example.farmer.farmer

import android.Manifest
import android.R
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.farmer.databinding.FragmentAddPointSaleBinding
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.SearchType
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.search.ToponymObjectMetadata
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider


class AddPointSaleFragment : Fragment(), CameraListener {

    private val viewModel: FarmerViewModel by activityViewModels {
        FarmerViewModelFactory(requireActivity().application)
    }

    private lateinit var userLocationLayer: UserLocationLayer
    private var _binding: FragmentAddPointSaleBinding? = null
    private val binding get() = _binding!!

    // 2. Менеджеры Яндекса
    private lateinit var searchManager: SearchManager
    private var searchSession: Session? = null

    // 3. Регистрация запроса разрешений
    private val permissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            smartLocationFetch()
        } else {
            Toast.makeText(context, "Доступ к GPS отклонен", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPointSaleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация сервисов поиска
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)

        // Подписка на движение камеры
        binding.mapView.mapWindow.map.addCameraListener(this)

        setupUserLocationLayer()
        setupListeners()
        smartLocationFetch() // Пытаемся найти пользователя при входе
    }

    /**
     * активация слоя пользователя: активация и стилизация
     */
    private fun setupUserLocationLayer() {
        val mapKit = MapKitFactory.getInstance()

        // Создаем слой местоположения
        userLocationLayer = mapKit.createUserLocationLayer(binding.mapView.mapWindow)
        userLocationLayer.isVisible = true

        // Если хотите свою иконку вместо синей точки:
        userLocationLayer.setObjectListener(object : UserLocationObjectListener {
            override fun onObjectAdded(view: UserLocationView) {
                smartLocationFetch()
                // в будущем можно добавить свои иконки
//                view.pin.setIcon(ImageProvider.fromResource(context, R.drawable.ic))
//                view.arrow.setIcon(ImageProvider.fromResource(context, R.drawable.ic))
            }
            override fun onObjectRemoved(view: UserLocationView) {}
            override fun onObjectUpdated(view: UserLocationView, event: ObjectEvent) {}
        })
    }

    /**
     * Установка слушателей для кнопок и ввода
     */
    private fun setupListeners() {
        binding.ibToMyLocation.setOnClickListener {
            smartLocationFetch()
        }

        binding.btnAddPointSale.setOnClickListener {
            val currentAddress = binding.searchEdit.text.toString()
            showAddPointNameDialog(currentAddress)
        }

        binding.searchEdit.setOnEditorActionListener { _, _, _ ->
            val query = binding.searchEdit.text.toString()
            if (query.isNotEmpty()) searchByAddress(query)
            false
        }
    }

    /**
     * Умное получение локации: проверяет разрешения и двигает карту
     */
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

    /**
     * Плавное перемещение камеры к указанной точке
     */
    private fun moveCameraToPoint(point: Point) {
        binding.mapView.mapWindow.map.move(
            CameraPosition(point, 16f, 0f, 0f),
            Animation(Animation.Type.SMOOTH, 1.5f),
            null
        )
    }

    /**
     * Обратное геокодирование: преобразует координаты в текст адреса
     */
    private fun reverseGeocode(point: Point) {
        searchSession?.cancel()
        val options = SearchOptions().apply { searchTypes = SearchType.GEO.value }

        searchSession = searchManager.submit(point, 16, options, object : Session.SearchListener {
            override fun onSearchResponse(response: Response) {
                val address = response.collection.children.firstOrNull()?.obj
                    ?.metadataContainer?.getItem(ToponymObjectMetadata::class.java)
                    ?.address?.formattedAddress
                binding.searchEdit.setText(address ?: "Неизвестное место")
            }

            override fun onSearchError(error: Error) {
                Log.e("MAP_ERROR", "Geocode error: $error")
            }
        })
    }

    /**
     * Поиск координат по текстовому адресу из строки поиска
     */
    private fun searchByAddress(query: String) {
        searchSession?.cancel()
        val visibleRegion = binding.mapView.mapWindow.map.visibleRegion
        val box = BoundingBox(visibleRegion.bottomLeft, visibleRegion.topRight)

        searchSession = searchManager.submit(
            query, Geometry.fromBoundingBox(box), SearchOptions(),
            object : Session.SearchListener {
                override fun onSearchResponse(response: Response) {
                    response.collection.children.firstOrNull()?.obj?.geometry?.get(0)?.point?.let {
                        moveCameraToPoint(it)
                    }
                }

                override fun onSearchError(error: Error) {}
            })
    }

    /**
     * Диалог для ввода названия точки перед сохранением
     */
    private fun showAddPointNameDialog(address: String) {
        val editText = EditText(requireContext()).apply { hint = "Название (н-р: Моя ферма)" }

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Сохранить точку")
            .setView(editText)
            .setPositiveButton("ОК") { _, _ ->
                val name = editText.text.toString()
                val coords = binding.mapView.mapWindow.map.cameraPosition.target
                savePoint(name, address, coords)
            }
            .setNegativeButton("Отмена", null).show()
    }

    private fun savePoint(name: String, address: String, point: Point) {
        // viewModel.addPoint(name, address, point.latitude, point.longitude)
        Toast.makeText(context, "Точка $name сохранена", Toast.LENGTH_SHORT).show()
        parentFragmentManager.popBackStack()
    }


    override fun onCameraPositionChanged(
        map: Map,
        pos: CameraPosition,
        reason: CameraUpdateReason,
        finished: Boolean
    ) {
        if (finished) reverseGeocode(pos.target)
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        binding.mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}