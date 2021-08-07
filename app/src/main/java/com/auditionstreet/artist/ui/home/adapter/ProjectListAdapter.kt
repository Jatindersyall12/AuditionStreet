package com.auditionstreet.artist.ui.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.auditionstreet.artist.R
import com.auditionstreet.artist.model.response.HomeApiResponse
import com.auditionstreet.artist.model.response.ProjectResponse
import com.auditionstreet.artist.utils.showToast
import kotlinx.android.synthetic.main.project_item.view.*
import java.util.*

class ProjectListAdapter(
    val mContext: FragmentActivity,private val mCallback: (
        mposition: Int
    ) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HomeApiResponse.Data.PendingRequest>() {

        override fun areItemsTheSame(
            oldItem: HomeApiResponse.Data.PendingRequest,
            newItem: HomeApiResponse.Data.PendingRequest
        ): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: HomeApiResponse.Data.PendingRequest,
            newItem: HomeApiResponse.Data.PendingRequest
        ): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ConnectionHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.project_item,
                parent,
                false
            ),
            mContext
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ConnectionHolder -> {
//                holder.bind(differ.currentList[position])
                holder.itemView.btnViewDetail.setOnClickListener{
                    showToast(mContext,mContext.resources.getString(R.string.str_coming_soon))
                }
                holder.itemView.tvActor.text = differ.currentList[position].title
                holder.itemView.tvAgeRange.text = "Age:"+differ.currentList[position].age
                holder.itemView.new_Height.text = differ.currentList[position].heightFt+"."+
                        differ.currentList[position].heightIn+" ft min."
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(projectResponse: List<HomeApiResponse.Data.PendingRequest>) {
        differ.submitList(projectResponse)
        notifyDataSetChanged()
    }

    class ConnectionHolder(
        itemView: View,
        val mContext: Context
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: HomeApiResponse.Data.PendingRequest) = with(itemView) {
            val rnd = Random()
            val color: Int = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
            itemView.btnViewDetail.background.setTint(color)
            //itemView.tvProject.setTextColor(color)
        }
    }
}
