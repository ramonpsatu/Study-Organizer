package com.ramonpsatu.studyorganizer.features.collections.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ramonpsatu.studyorganizer.features.collections.listeners.ToggleClickListener
import com.ramonpsatu.studyorganizer.features.collections.model.TopicItem
import com.ramonpsatu.studyorganizer.features.collections.databinding.TopicContentLayoutBinding


/**
 * RecyclerView adapter for displaying a list of topics.
 *
 * The UI is based on the [TopicContentLayoutBinding].
 * We use the [TopicItem] as a model for the binding.
 */
class TopicListAdapter(
    private val itemClickEdit: (Int) -> Unit,
    private val itemClickDescription: (Int) -> Unit,
    private val toggleClickListener: ToggleClickListener,
    private val itemClickPerformance: (Int) -> Unit

) :
    RecyclerView.Adapter<TopicListAdapter.ViewHolder>() {

    private val asyncListDiff = AsyncListDiffer(this, DiffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = TopicContentLayoutBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(
            binding,
            itemClickEdit,
            itemClickDescription,
            toggleClickListener,
            itemClickPerformance
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(asyncListDiff.currentList[position])

    }

    override fun getItemCount(): Int {
        return asyncListDiff.currentList.size
    }


    fun updateTopicList(topicItem: List<TopicItem>) {

        asyncListDiff.submitList(topicItem)
    }

   inner class ViewHolder(
       private val binding: TopicContentLayoutBinding,
       private val itemClickEdit: (Int) -> Unit,
       private val itemClickDescription: (Int) -> Unit,
       private val toggleClickListener: ToggleClickListener,
       private val itemClickPerformance: (Int) -> Unit
   ) :
       RecyclerView.ViewHolder(binding.root) {

       init {
           binding.icEditTopic.setOnClickListener { itemClickEdit(adapterPosition) }
           binding.root.setOnClickListener { itemClickDescription(adapterPosition) }
           binding.viewTouchableBackground.setOnClickListener {
               itemClickPerformance(
                   adapterPosition
               )
           }
       }


        fun bind(topicItem: TopicItem) {

            val str = String.format("%.0f", topicItem.performance) + "%"

            binding.editTextTitle.text = topicItem.title
            binding.textViewErrorNumber.text = topicItem.error.toString()
            binding.textViewMatchNumber.text = topicItem.match.toString()
            binding.textViewQuestionsNumber.text = topicItem.amountOfQuestions.toString()
            binding.textViewPerformanceNumber.text = str

            binding.icTopicCheckbox.setOnClickListener {

                if (topicItem.isCompleted == 0) {
                    topicItem.isCompleted = 1

                } else if (topicItem.isCompleted == 1) {
                    topicItem.isCompleted = 0

                }
                toggle(topicItem.isCompleted)
                toggleClickListener.updateToggle(topicItem.isCompleted, topicItem.id,adapterPosition)

            }

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

                binding.imageViewIcBackgroundTaskBook.background = ContextCompat.getDrawable(
                    itemView.context,
                    com.ramonpsatu.studyorganizer.core.ui.R.drawable.ic_background_field_completed
                )
                binding.icEditTopic.isClickable = false
                binding.root.isClickable = false
                binding.viewTouchableBackground.isClickable = false

            } else {
                binding.icTopicCheckbox.setImageDrawable(
                    AppCompatResources.getDrawable(
                        itemView.context,
                        com.ramonpsatu.studyorganizer.core.ui.R.drawable.ic_checkbox
                    )
                )
                binding.imageViewIcBackgroundTaskBook.background =
                    ContextCompat.getDrawable(itemView.context, com.ramonpsatu.studyorganizer.core.ui.R.drawable.ic_background_field_userdata)
                binding.viewTouchableBackground.isClickable = true
                binding.icEditTopic.isClickable = true
                binding.root.isClickable = true
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