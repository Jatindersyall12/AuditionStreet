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
import kotlinx.android.synthetic.main.application_item.view.*
import kotlinx.android.synthetic.main.application_item.view.tvActress
import kotlinx.android.synthetic.main.application_item.view.tvAge
import kotlinx.android.synthetic.main.application_item.view.tvHeight
import kotlinx.android.synthetic.main.application_item.view.tvName
import kotlinx.android.synthetic.main.application_item.view.tvViewProfile
import kotlinx.android.synthetic.main.home_shortlist_item.view.*
import kotlinx.android.synthetic.main.project_item.view.*
import java.util.*

class HomeShortListAdapter(
    val mContext: FragmentActivity, private val mCallback: (
        mposition: Int,
        isViewProfileClicked: Boolean
    ) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HomeApiResponse.Data.Accept>() {

        override fun areItemsTheSame(
            oldItem: HomeApiResponse.Data.Accept,
            newItem: HomeApiResponse.Data.Accept
        ): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: HomeApiResponse.Data.Accept,
            newItem: HomeApiResponse.Data.Accept
        ): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ConnectionHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.home_shortlist_item,
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
                holder.itemView.tvViewProfile.setOnClickListener {
                    mCallback.invoke(position, true)
                }
                holder.itemView.tvChat.setOnClickListener {
                    mCallback.invoke(position, false)
                }
                holder.itemView.tvName.text = differ.currentList[position].title
                if (differ.currentList[position].projectgender.equals("Male")){
                    holder.itemView.tvActress.text = mContext.getString(R.string.actor)
                }else{
                    holder.itemView.tvActress.text = mContext.getString(R.string.actress)
                }
                holder.itemView.tvAge.text = "Age:"+differ.currentList[position].projectAge
                holder.itemView.tvHeight.text = "Height: "+differ.currentList[position].projectheightFt+
                        "."+differ.currentList[position].projectheightIn+"ft"
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(projectResponse: List<HomeApiResponse.Data.Accept>) {
        differ.submitList(projectResponse)
        notifyDataSetChanged()
    }

    class ConnectionHolder(
        itemView: View,
        val mContext: Context
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: HomeApiResponse.Data.Accept) = with(itemView) {
            val rnd = Random()
            val color: Int = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
            itemView.btnViewDetail.background.setTint(color)
            //itemView.tvProject.setTextColor(color)
        }
    }
}
