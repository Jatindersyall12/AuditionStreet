package com.auditionstreet.artist.ui.home.adapter

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
import com.auditionstreet.artist.model.response.MyProjectResponse
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultAllocator
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.all_application_item.view.*

@Suppress("DEPRECATION")
class AllApplicationsAdapter(
    val mContext: FragmentActivity, private val mCallback: (
        mposition: Int
    ) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MyProjectResponse.Data>() {

        override fun areItemsTheSame(
            oldItem: MyProjectResponse.Data,
            newItem: MyProjectResponse.Data
        ): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: MyProjectResponse.Data,
            newItem: MyProjectResponse.Data
        ): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ConnectionHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.all_application_item,
                parent,
                false
            ),
            mContext
        )
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        /*if (holder.itemView.player_view.player != null) {
            holder.itemView.player_view!!.player!!.playWhenReady = false
            holder.itemView.player_view!!.player!!.stop()
            holder.itemView.player_view!!.player!!.release()
            holder.itemView.player_view.player = null

        }*/
        super.onViewDetachedFromWindow(holder)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ConnectionHolder -> {
                holder.bind(differ.currentList[position])
                holder.itemView.tvTitle.text = differ.currentList[position].title
                holder.itemView.tvAgeDetail.text = differ.currentList[position].age
                holder.itemView.tvHeightDetail.text = differ.currentList[position].heightFt+"."+
                        differ.currentList[position].heightIn
                holder.itemView.tvLanguageDetail.text = differ.currentList[position].lang
                holder.itemView.tvLocationDetail.text = differ.currentList[position].location
                holder.itemView.tvDescDetail.text = differ.currentList[position].description
                holder.itemView.tvGender.text = differ.currentList[position].gender
                holder.itemView.btnViewProfile.setOnClickListener {
                    mCallback.invoke(0)
                }
                holder.itemView.btnReport.setOnClickListener {
                    mCallback.invoke(1)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(projectResponse: ArrayList<MyProjectResponse.Data>) {
        differ.submitList(projectResponse)
        notifyDataSetChanged()
    }

    class ConnectionHolder(
        itemView: View,
        val mContext: Context
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: MyProjectResponse.Data) = with(itemView) {
        }
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {


        }
    }
}