package dev.mfaheemezani.mvvm.ui.tophome

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import dev.mfaheemezani.mvvm.R

import dev.mfaheemezani.mvvm.data.network.response.Result;
import dev.mfaheemezani.mvvm.helpers.DateTime

class TopHomeItemsAdapter(private var listOfResults: List<Result>) : RecyclerView.Adapter<TopHomeItemsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llItem: LinearLayoutCompat = itemView.findViewById(R.id.ll_item)
        val tvStoryTitle: AppCompatTextView = itemView.findViewById(R.id.tv_story_title)
        val vSkeletonTitle: View = itemView.findViewById(R.id.v_skeleton_title)
        val tvArticlePubDate: AppCompatTextView = itemView.findViewById(R.id.tv_article_published_date)
        val vSkeletonDate: View = itemView.findViewById(R.id.v_skeleton_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val documentView = inflater.inflate(R.layout.item_article, parent, false)
        return ViewHolder(documentView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result: Result = listOfResults[position]
        val llItem = holder.llItem
        val tvTitle = holder.tvStoryTitle
        val vSkeletonTitle = holder.vSkeletonTitle
        val tvPubDate = holder.tvArticlePubDate
        val vSkeletonDate = holder.vSkeletonDate

        when (result.title.isNullOrBlank()) {
            true -> {
                tvTitle.visibility = View.GONE
                vSkeletonTitle.visibility = View.VISIBLE
            }
            else -> {
                // Title
                tvTitle.visibility = View.VISIBLE
                vSkeletonTitle.visibility = View.GONE
                tvTitle.text = result.title.toString()

                // Published Date
                tvPubDate.visibility = View.VISIBLE
                vSkeletonDate.visibility = View.GONE
                tvPubDate.text = DateTime.convertToDateTimeHumanReadableFormat(result.published_date!!)

                llItem.setOnClickListener {
                    it.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(result.url)))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return listOfResults.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(listOfDocs: List<Result>) {
        this.listOfResults = listOfDocs
        notifyDataSetChanged()
    }

}