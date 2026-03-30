package com.example.farmer.chat

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.farmer.R
import com.example.farmer.databinding.FragmentChatBinding
import kotlinx.coroutines.launch


class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatViewModel by viewModels {
        ChatViewModelFactory(requireContext())
    }
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val farmerName = arguments?.getString("FARM_NAME")

        val senderID = arguments?.getLong("SENDER_ID")
        val orderID = arguments?.getLong("ORDER_ID")
        val receiverId = arguments?.getLong("RECEIVER_ID")

        if (senderID == 0L || receiverId == 0L || orderID == 0L) {
            Log.e("ChatFragment", "❌ Ошибка: не все ID переданы!")
            Toast.makeText(
                requireActivity(), "Не все айди переданы, чат не сможет быть открыт",
                Toast.LENGTH_SHORT
            ).show()
            findNavController().popBackStack()
        }

        binding.tvFarmName.text = farmerName
        binding.btnCamera.setOnClickListener {
            showAttachmentMenu()
        }

        setupRecyclerView(senderID!!)
        observeViewModel()

        binding.btnSendMessage.setOnClickListener {
            val text = binding.etMessageInput.text.toString()
            if (text.isNotBlank()) {
                viewModel.sendText(orderID!!, senderID!!, receiverId!!, text)
                binding.etMessageInput.text.clear()
            }
        }

        // Инициализируем чат (загрузка истории + подключение к сокетам)
        viewModel.initChat(orderID ?: -1L)
    }

    private fun setupRecyclerView(senderId: Long) {
        // Передаем ID текущего юзера, чтобы адаптер знал, чьи сообщения справа
        chatAdapter = ChatAdapter(senderId) { imageUrl ->
            // Тут можно открыть фото на весь экран
        }

        binding.rvChatMessages.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true // Список всегда прижат к низу
            }
        }
    }

    private fun observeViewModel() {
        // Подписываемся на список сообщений
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.messages.collect { messages ->
                    Log.d("VM messages collet", "Список сообщений: $messages")
                    chatAdapter.submitList(messages) {
                        // Прокручиваем вниз только когда список обновился
                        if (messages.isNotEmpty()) {
                            binding.rvChatMessages.scrollToPosition(messages.size - 1)
                        }

                        val currentUserId = arguments?.getLong("SENDER_ID") ?: 0L
                        val orderId = arguments?.getLong("ORDER_ID") ?: 0L
                        val receiverId = arguments?.getLong("RECEIVER_ID")?.toLong()
                        viewModel.markMessagesAsRead(orderId, currentUserId, receiverId!!)
                    }
                }
            }

            launch {
                viewModel.error.collect { error ->
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun showAttachmentMenu() {
        // 1. Создаем PopupMenu, привязанное к невидимому якорю (чтобы всплывало НАД кнопкой)
        val popupMenu = PopupMenu(requireContext(), binding.menuAnchor)

        // 2. Наполняем меню пунктами (можно программно, чтобы не плодить XML файлы)
        popupMenu.menu.add(0, 1, 0, "Сделать фото").apply {
            //setIcon(R.drawable.ic_camera_green)
        }
        popupMenu.menu.add(0, 2, 1, "Выбрать из галереи").apply {
            //setIcon(R.drawable.ic_image_green) // Добавь иконку в drawable
        }

        // 3. Обработка кликов по пунктам меню
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    // Логика "Сделать фото" (открыть камеру)
                    //openCamera()
                    true
                }

                2 -> {
                    // Логика "Галерея" (открыть выбор фото)
                    //openGallery()
                    true
                }

                else -> false
            }
        }

        // 4. Показываем меню
        popupMenu.show()
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChatFragment().apply {
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}