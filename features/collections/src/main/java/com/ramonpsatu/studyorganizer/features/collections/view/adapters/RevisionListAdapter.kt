package com.ramonpsatu.studyorganizer.features.collections.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ramonpsatu.studyorganizer.features.collections.databinding.RevisionContentLayoutBinding
import com.ramonpsatu.studyorganizer.features.collections.listeners.ToggleClickListener
import com.ramonpsatu.studyorganizer.features.collections.model.RevisionItem
import com.ramonpsatu.studyorganizer.features.collections.utils.MyCalendar
import java.util.Calendar
import java.util.Date

class RevisionListAdapter(private val toggleClickListener: ToggleClickListener
                           ) : RecyclerView.Adapter<RevisionListAdapter.ViewHolder>() {

    private val asyncListDiffer = AsyncListDiffer(this, DiffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RevisionContentLayoutBinding.inflate(inflater, parent, false)
        return ViewHolder(binding,toggleClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(asyncListDiffer.currentList[position])
    }


    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    fun bindVersionLists(fridayList: List<RevisionItem>) {

        asyncListDiffer.submitList(fridayList)
    }

    inner class ViewHolder(
        private val binding: RevisionContentLayoutBinding,
        private val toggleClickListener: ToggleClickListener
    ) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(revisions: RevisionItem) {
            val calendar = Calendar.getInstance()
           calendar.timeInMillis = revisions.date

            binding.textViewRevisionData.text = MyCalendar.formatDateByOneView(
                Date(revisions.date),calendar.get(Calendar.DAY_OF_WEEK),itemView.context
            )
            binding.textViewTextTitle.text = revisions.title
            binding.textViewRevisionSchedule.text = revisions.schedule


            binding.checkBox.setOnClickListener {
                if (revisions.isCompleted == 0) {
                    revisions.isCompleted = 1


                } else{
                    revisions.isCompleted = 0


                }
                binding.checkBox.isChecked = revisions.isCompleted != 0
                toggleClickListener.updateToggle(revisions.isCompleted, revisions.id,adapterPosition)
            }


            binding.checkBox.isChecked = revisions.isCompleted != 0

        }


    }

    private object DiffCallback : DiffUtil.ItemCallback<RevisionItem>() {

        override fun areItemsTheSame(oldItem: RevisionItem, newItem: RevisionItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RevisionItem, newItem: RevisionItem): Boolean {
            return (oldItem.title == newItem.title) && (oldItem.isCompleted == newItem.isCompleted)
        }


    }

}