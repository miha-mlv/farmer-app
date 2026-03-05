package com.example.farmer.farmer

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.farmer.R
import com.example.farmer.common.util.TokenManager
import com.example.farmer.databinding.FragmentAddNewProductBinding
import com.example.farmer.databinding.FragmentMyProductBinding
import com.example.farmer.farmer.network.ProductRequest
import com.example.farmer.farmer.network.RetrofitClientFarmer
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddNewProductFragment : Fragment() {

    private val viewModel: FarmerViewModel by viewModels() {
        FarmerViewModelFactory(
            mapOf(
                FarmerViewModel::class.java to {
                    FarmerViewModel(RetrofitClientFarmer.instance, requireActivity().application)
                }
            )
        )
    }

    private val tokenManager: TokenManager by lazy{
        TokenManager(activity?.application ?: throw Exception("Ошибка токен менеджера"))
    }
    private var _binding: FragmentAddNewProductBinding? = null
    private val binding get() = _binding!!

    private val selectedImageUris = mutableListOf<Uri>()

    private val pickImagesLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            if (uris.isNotEmpty()) {
                selectedImageUris.clear()
                selectedImageUris.addAll(uris.take(3))
                updatePhotoUI()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddNewProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSpinnerCategories()
        binding.spinnerCategory.setOnClickListener {
            binding.spinnerCategory.showDropDown()
        }

        binding.btnChoicePhoto.setOnClickListener { pickImagesLauncher.launch("image/*") }

        binding.btnSave.setOnClickListener { setOnClickBtnSave() }
        observeViewModel()


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddNewProductFragment().apply {}
    }

    private fun setOnClickBtnSave() {
        val selectCategory = getSelectedCategory()
        val name = binding.etProductName.text.toString()
        val price = binding.etPrice.toString().toDoubleOrNull() ?: 0.0
        val quantity = binding.etQty.text.toString().toIntOrNull() ?: 0
        val description = binding.etDescription.text.toString()

        val request = ProductRequest(
            name = name,
            category = selectCategory,
            price = price,
            quantity = quantity,
            description = description,
            farmerToken = tokenManager.getToken().toString()
        )

        val json = Gson().toJson(request)
        val productDataBody = json.toRequestBody("application/json".toMediaTypeOrNull())

        val imageParts = selectedImageUris.map { uri ->
            prepareImagePart(uri)
        }

        viewModel.saveProduct(productDataBody, imageParts)


    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            // repeatOnLifecycle дождется, пока фрагмент станет активным
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Подписываемся на состояние загрузки
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.btnSave.isEnabled = !isLoading
                        // Можно показать/скрыть ProgressBar
                    }
                }

                // Подписываемся на успех
                launch {
                    viewModel.isSuccess.collect {
                        Toast.makeText(requireContext(), "Товар добавлен!", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    }
                }

                // Подписываемся на ошибки
                launch {
                    viewModel.error.collect { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun prepareImagePart(uri: Uri): MultipartBody.Part {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val tempFile = File(requireContext().cacheDir, "temp_${System.currentTimeMillis()}.jpg")
        tempFile.outputStream().use { inputStream?.copyTo(it) }

        val requestFile = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("images", tempFile.name, requestFile)
    }

    private fun getSelectedCategory(): String {
        val selectedDisplayName = binding.spinnerCategory.text.toString()
        val categoryEnum = ProductCategory.entries.find { it.displayName == selectedDisplayName }
        return categoryEnum?.name ?: "OTHER"
    }

    private fun initSpinnerCategories() {
        val categoryNames = ProductCategory.entries.map { it.displayName }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            categoryNames
        )

        binding.spinnerCategory.setAdapter(adapter)
    }

    private fun updatePhotoUI() {
        binding.photoContainer.removeAllViews()

        val sizeInPx = 100.dpToPx(requireContext())
        val marginInPx = 4.dpToPx(requireContext())

        selectedImageUris.forEach { uri ->
            val cardView =
                com.google.android.material.card.MaterialCardView(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(sizeInPx, sizeInPx).apply {
                        setMargins(marginInPx, marginInPx, marginInPx, marginInPx)
                    }
                    radius = 16.dpToPx(requireContext()).toFloat()
                    cardElevation = 0f
                    strokeWidth = 1.dpToPx(requireContext())
                }

            val imageView = ImageView(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = ImageView.ScaleType.CENTER_CROP
                setImageURI(uri)
            }

            cardView.addView(imageView)
            binding.photoContainer.addView(cardView)
        }
    }

    enum class ProductCategory(val displayName: String) {
        VEGETABLES("Овощи"),
        FRUITS("Фрукты"),
        DAIRY("Молочные продукты"),
        MEAT("Мясо"),
        GRAINS("Зерновые"),
        OTHER("Другое")
    }

    fun Int.dpToPx(context: android.content.Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }
}