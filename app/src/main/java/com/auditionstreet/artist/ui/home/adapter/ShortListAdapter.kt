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
import com.auditionstreet.artist.model.response.ProjectResponse
import com.auditionstreet.artist.utils.showToast
import kotlinx.android.synthetic.main.project_item.view.*
import kotlinx.android.synthetic.main.shortlist_item.view.*
import java.util.*

class ShortListAdapter(
    val mContext: FragmentActivity, private val mCallback: (
        mposition: Int
    ) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ProjectResponse.Data>() {

        override fun areItemsTheSame(
            oldItem: ProjectResponse.Data,
            newItem: ProjectResponse.Data
        ): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: ProjectResponse.Data,
            newItem: ProjectResponse.Data
        ): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ConnectionHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.shortlist_item,
                parent,
                false
            ),
            mContext
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ConnectionHolder -> {
                holder.itemView.imgFavourite.setOnClickListener(this)
                holder.itemView.tvChat.setOnClickListener(this)
                holder.itemView.tvViewProfile.setOnClickListener(this)

//                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        //return differ.currentList.size
        return 6
    }

    fun submitList(projectResponse: List<ProjectResponse.Data>) {
        differ.submitList(projectResponse)
        notifyDataSetChanged()
    }

    class ConnectionHolder(
        itemView: View,
        val mContext: Context
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: ProjectResponse.Data) = with(itemView) {
            val rnd = Random()
            val color: Int = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
            itemView.btnViewDetail.background.setTint(color)
            //itemView.tvProject.setTextColor(color)
        }
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.imgFavourite -> {
                showToast(mContext, mContext.resources.getString(R.string.str_coming_soon))
            }
            R.id.tvChat -> {
                showToast(mContext, mContext.resources.getString(R.string.str_coming_soon))

            }
            R.id.tvViewProfile -> {
                showToast(mContext, mContext.resources.getString(R.string.str_coming_soon))

            }
        }
    }
}
