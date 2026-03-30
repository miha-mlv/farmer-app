package com.example.farmer.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.farmer.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val BASE_IMAGE_URL = "http://10.0.2.2:8080" // Убери лишний слэш в конце, если в imageUrl он уже есть

class ChatAdapter(
    private val currentUserId: Long,
    private val onImageClick: (String) -> Unit // Бонус: клик по картинке для увеличения
) : ListAdapter<Message, RecyclerView.ViewHolder>(MessageDiffCallback) {

    companion object {
        private const val TYPE_SENT = 1
        private const val TYPE_RECEIVED = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).senderId == currentUserId) TYPE_SENT else TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_SENT) {
            val view = inflater.inflate(R.layout.item_chat_message_user, parent, false)
            SentViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_chat_message_receiver, parent, false)
            ReceivedViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        when (holder) {
            is SentViewHolder -> holder.bind(message)
            is ReceivedViewHolder -> holder.bind(message)
        }
    }

    // --- ViewHolder для твоих сообщений ---
    inner class SentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val text = view.findViewById<TextView>(R.id.tvMessageText)
        private val image = view.findViewById<ImageView>(R.id.ivMessageImage)
        private val time = view.findViewById<TextView>(R.id.tvMessageTime)
        private val statusIcon = view.findViewById<ImageView>(R.id.ivStatus)

        fun bind(message: Message) {
            text.visibility = if (message.content.isNullOrEmpty()) View.GONE else View.VISIBLE
            text.text = message.content

            if (!message.imageUrl.isNullOrEmpty()) {
                image.visibility = View.VISIBLE
                Glide.with(itemView.context)
                    .load("$BASE_IMAGE_URL${message.imageUrl}")
                    .into(image)
                image.setOnClickListener { onImageClick(message.imageUrl) }
            } else {
                image.visibility = View.GONE
            }

            time.text = SimpleDateFormat("HH:mm", Locale("ru")).format(Date(message.timestamp))

            // Галочки статуса
            if (message.isRead) {
                statusIcon.setImageResource(R.drawable.ic_read)
            } else {
                statusIcon.setImageResource(R.drawable.ic_not_read)
            }
        }
    }

    // --- ViewHolder для входящих сообщений ---
    inner class ReceivedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val text = view.findViewById<TextView>(R.id.tvMessageText)
        private val image = view.findViewById<ImageView>(R.id.ivMessageImage)
        private val time = view.findViewById<TextView>(R.id.tvMessageTime)

        fun bind(message: Message) {
            text.visibility = if (message.content.isNullOrEmpty()) View.GONE else View.VISIBLE
            text.text = message.content

            if (!message.imageUrl.isNullOrEmpty()) {
                image.visibility = View.VISIBLE
                Glide.with(itemView.context)
                    .load("$BASE_IMAGE_URL${message.imageUrl}")
                    .into(image)
                image.setOnClickListener { onImageClick(message.imageUrl) }
            } else {
                image.visibility = View.GONE
            }

            time.text = SimpleDateFormat("HH:mm", Locale("ru")).format(Date(message.timestamp))
        }
    }

    // --- Логика DiffUtil ---
    object MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            // Если у сообщения уже есть ID из БД — сравниваем по нему,
            // если нет (только отправили) — по времени
            return if (oldItem.id != null && newItem.id != null) {
                oldItem.id == newItem.id
            } else {
                oldItem.timestamp == newItem.timestamp
            }
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            // Важно сравнивать все поля, влияющие на визуализацию
            return oldItem.content == newItem.content &&
                    oldItem.isRead == newItem.isRead &&
                    oldItem.timestamp == newItem.timestamp
        }
    }
}