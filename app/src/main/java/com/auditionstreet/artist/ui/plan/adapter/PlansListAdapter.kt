package com.auditionstreet.artist.ui.plan.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.auditionstreet.artist.R
import com.auditionstreet.artist.model.response.MyProjectResponse
import com.auditionstreet.artist.model.response.PlansListResponse
import kotlinx.android.synthetic.main.plan_list_item.view.*
import java.util.*

class PlansListAdapter(
    val mContext: FragmentActivity, private val mCallback: (
        mposition: String
    ) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PlansListResponse.Data>() {

        override fun areItemsTheSame(
            oldItem: PlansListResponse.Data,
            newItem: PlansListResponse.Data
        ): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: PlansListResponse.Data,
            newItem: PlansListResponse.Data
        ): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ConnectionHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.plan_list_item,
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
                holder.itemView.btnBuyPlan.setOnClickListener {
                    mCallback.invoke(position.toString())
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(planList: List<PlansListResponse.Data>) {
        differ.submitList(planList)
        notifyDataSetChanged()
    }

    class ConnectionHolder(
        itemView: View,
        val mContext: Context
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: PlansListResponse.Data) = with(itemView) {
            val rnd = Random()
            itemView.tvPlan.text = "Plan: "+item.name
            itemView.tvNoOfRequests.text = "Request: "+item.requests
            itemView.tvPriceValue.text = context.resources.getString(R.string.Rs)+" "+
                    item.price
            var color: Int
            var status: String
           /* if (item.castingStatus == "0") {
                color = ContextCompat.getColor(mContext, R.color.yellow)
                status = resources.getString(R.string.tv_pending)
            } else if (item.castingStatus == "1") {
                color = ContextCompat.getColor(mContext, R.color.green)
                status = resources.getString(R.string.tv_accepted)
            } else {
                color = ContextCompat.getColor(mContext, R.color.red)
                status = resources.getString(R.string.tv_rejected)
            }*/
            //  val color: Int = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
            //itemView.btnViewDetail.background.setTint(color)
            //itemView.tvProject.setTextColor(color)
           /* itemView.tvStatusDetail.setTextColor(color)
            itemView.tvStatusDetail.setText(status)*/

        }
    }
}
