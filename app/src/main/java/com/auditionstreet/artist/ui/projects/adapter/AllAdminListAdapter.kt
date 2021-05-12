package com.auditionstreet.artist.ui.projects.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.auditionstreet.artist.R
import com.auditionstreet.artist.model.response.AllAdminResponse
import kotlinx.android.synthetic.main.all_admin_item.view.*

class AllAdminListAdapter(
    val mContext: FragmentActivity, private val mCallback: (
        mposition: String
    ) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AllAdminResponse.Data>() {

        override fun areItemsTheSame(
            oldItem: AllAdminResponse.Data,
            newItem: AllAdminResponse.Data
        ): Boolean {
            return oldItem == newItem

            //return false
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: AllAdminResponse.Data,
            newItem: AllAdminResponse.Data
        ): Boolean {
            return oldItem == newItem
            // return false
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ConnectionHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.all_admin_item,
                parent,
                false
            ),
            mContext
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ConnectionHolder -> {
                holder.bind(differ.currentList[position])
                // holder.itemView.chkUser.isChecked=differ.currentList[position].isChecked


            }
        }
    }

    /* override fun getItemViewType(position: Int): Int {
         return 1
     }*/

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(projectResponse: List<AllAdminResponse.Data?>?) {
        differ.submitList(projectResponse)
        notifyDataSetChanged()
    }

    inner class ConnectionHolder(
        itemView: View,
        val mContext: Context
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: AllAdminResponse.Data) = with(itemView) {
            itemView.chkUser.setOnCheckedChangeListener { buttonView, isChecked ->
                differ.currentList[adapterPosition].is_checked = isChecked
            }
            itemView.chkUser.isChecked = differ.currentList[adapterPosition].is_checked
            itemView.tvAdmin.text = item.name
        }
    }
}
