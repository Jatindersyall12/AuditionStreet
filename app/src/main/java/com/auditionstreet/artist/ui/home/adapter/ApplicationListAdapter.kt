package com.auditionstreet.artist.ui.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
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
import kotlinx.android.synthetic.main.project_item.view.*
import java.util.*

class ApplicationListAdapter(
    val mContext: FragmentActivity,private val mCallback: (
        mposition: Int
    ) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HomeApiResponse.Data.Project>() {

        override fun areItemsTheSame(
            oldItem: HomeApiResponse.Data.Project,
            newItem: HomeApiResponse.Data.Project
        ): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: HomeApiResponse.Data.Project,
            newItem: HomeApiResponse.Data.Project
        ): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ConnectionHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.application_item,
                parent,
                false
            ),
            mContext
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ConnectionHolder -> {
                holder.itemView.tvViewProfile.setOnClickListener{
                    mCallback.invoke(position)
                }
               // holder.bind(differ.currentList[position])
                holder.itemView.tvName.text = differ.currentList[position].title
                if (differ.currentList[position].gender.equals("Male")){
                    holder.itemView.tvActress.text = mContext.getString(R.string.actor)
                }else{
                    holder.itemView.tvActress.text = mContext.getString(R.string.actress)
                }
                holder.itemView.tvAge.text = "Age:"+differ.currentList[position].age
                holder.itemView.tvHeight.text = "Height: "+differ.currentList[position].heightFt+
                        "."+differ.currentList[position].heightIn+"ft"
                /*Glide.with(mContext).load(differ.currentList[position].a)
                    .into(holder.itemView.imgRound)*/
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(projectResponse: List<HomeApiResponse.Data.Project>) {
        differ.submitList(projectResponse)
        notifyDataSetChanged()
    }

    class ConnectionHolder(
        itemView: View,
        val mContext: Context
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: HomeApiResponse.Data.Project) = with(itemView) {
            Log.e("sd1","Dg")

            val rnd = Random()
            val color: Int = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
            itemView.btnViewDetail.background.setTint(color)
            //itemView.tvProject.setTextColor(color)
        }
    }
}
