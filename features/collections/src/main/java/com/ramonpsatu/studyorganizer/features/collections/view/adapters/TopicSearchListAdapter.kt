package com.ramonpsatu.studyorganizer.features.collections.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ramonpsatu.studyorganizer.features.collections.utils.MyCalendar
import com.ramonpsatu.studyorganizer.features.collections.model.TopicItem
import com.ramonpsatu.studyorganizer.features.collections.databinding.TopicContentShowInSearchListBinding
import java.util.Calendar
import java.util.TimeZone

class TopicSearchListAdapter(private val itemClickDescription: (Int) -> Unit) :
    RecyclerView.Adapter<TopicSearchListAdapter.ViewHolder>() {

    private val asyncListDiffer = AsyncListDiffer(this, DiffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = TopicContentShowInSearchListBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding, itemClickDescription)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(asyncListDiffer.currentList[position])
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    fun updateTopicList(topicList: List<TopicItem>) {

        asyncListDiffer.submitList(topicList)
    }


    inner class ViewHolder(
        private val binding: TopicContentShowInSearchListBinding,
        private val itemClickDescription: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.icShowDetails.setOnClickListener { itemClickDescription(adapterPosition) }
        }

        private val calendar = Calendar.getInstance(TimeZone.getDefault())
        fun bind(topicItem: TopicItem) {

            val year = topicItem.date.substring(0..3).toInt()
            val month = topicItem.date.substring(4..5).toInt()-1
            val dayOfWeek =  topicItem.date.substring(6 until topicItem.date.length).toInt()

            calendar.set(year, month , dayOfWeek)
            val day = calendar.get(Calendar.DAY_OF_WEEK)

            binding.editTextTitle.text = topicItem.title
            binding.textViewDate.text = MyCalendar.formatDateByOneView(
                calendar.time,
                day,
                itemView.context
            )

            toggle(topicItem.isCompleted)

        }

        private fun toggle(isCompleted: Int) {


            if (isCompleted == 1) {
                binding.icTopicCheckbox.setImageDrawable(
                    AppCompatResources.getDrawable(
                        itemView.context,
                        com.ramonpsatu.studyorganizer.core.ui.R.drawable.ic_checkbox_completed
                    )
                )
                binding.imageViewIcBackgroundTaskBook.background =
                    ContextCompat.getDrawable(
                        itemView.context,
                        com.ramonpsatu.studyorganizer.core.ui.R.drawable.ic_background_field_completed
                    )


            } else {
                binding.icTopicCheckbox.setImageDrawable(
                    AppCompatResources.getDrawable(
                        itemView.context,
                        com.ramonpsatu.studyorganizer.core.ui.R.drawable.ic_checkbox
                    )
                )
                binding.imageViewIcBackgroundTaskBook.background =
                    ContextCompat.getDrawable(
                        itemView.context,
                        com.ramonpsatu.studyorganizer.core.ui.R.drawable.ic_background_field_userdata
                    )

            }


        }


    }

    private object DiffCallback : DiffUtil.ItemCallback<TopicItem>() {
        override fun areItemsTheSame(oldItem: TopicItem, newItem: TopicItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TopicItem, newItem: TopicItem): Boolean {
            return (oldItem.title == newItem.title) && (oldItem.isCompleted == newItem.isCompleted)
        }

    }
}