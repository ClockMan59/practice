package ci.nsu.mobile.main.ui.users

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ci.nsu.mobile.main.R
import ci.nsu.mobile.main.data.model.UserDto

class UserAdapter(
    private val onClick: (UserDto) -> Unit
) : ListAdapter<UserDto, UserAdapter.UserViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class UserViewHolder(
        itemView: View,
        private val onClick: (UserDto) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.tvUserName)
        private val loginText: TextView = itemView.findViewById(R.id.tvUserLogin)
        private val emailText: TextView = itemView.findViewById(R.id.tvUserEmail)

        fun bind(item: UserDto) {
            nameText.text = item.fullName()
            loginText.text = "Логин: ${item.login}"
            emailText.text = item.email
            itemView.setOnClickListener { onClick(item) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<UserDto>() {
        override fun areItemsTheSame(oldItem: UserDto, newItem: UserDto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserDto, newItem: UserDto): Boolean {
            return oldItem == newItem
        }
    }
}
