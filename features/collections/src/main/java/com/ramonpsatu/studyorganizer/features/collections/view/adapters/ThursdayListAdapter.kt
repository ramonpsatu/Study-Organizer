package com.ramonpsatu.studyorganizer.features.collections.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ramonpsatu.studyorganizer.features.collections.viewmodels.SubjectListViewModel
import com.ramonpsatu.studyorganizer.features.collections.databinding.SubjectShowInCalendarLayoutBinding
import com.ramonpsatu.studyorganizer.features.collections.model.SubjectItem
import kotlinx.coroutines.launch


class ThursdayListAdapter(
    private val itemClick: (Int) -> Unit,
    private val viewModel: SubjectListViewModel
) : RecyclerView.Adapter<ThursdayListAdapter.ViewHolder>() {

    private val asyncListDiffer = AsyncListDiffer(this, DiffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SubjectShowInCalendarLayoutBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(asyncListDiffer.currentList[position])
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    fun updateSubjectThursday(thursdayList: List<SubjectItem>) {

        asyncListDiffer.submitList(thursdayList)

    }


    inner class ViewHolder(
        private val binding: SubjectShowInCalendarLayoutBinding,
        private val itemClick: (Int) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.imageViewIcBackgroundTaskBook.setOnClickListener { itemClick(adapterPosition) }

        }

        fun bind(subject: SubjectItem) {

            binding.editTextTitle.text = subject.title
            binding.imageViewIcBackgroundTaskBook.background.setTint(
                subject.backgroundColor
            )
            binding.textViewPerformanceNumber.text =
                itemView.context.getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_performance_number)

            viewModel.viewModelScope.launch {
                //Defines the text number of topics
                val date = android.text.format.DateFormat.format("yyyyMMdd",viewModel.stateOnceDates().value!!.datesOfWeek[3]).toString()

                val topicsBySubjectByDate = viewModel.getNumberTopicBySubjectByDate(
                    subject.id, date
                )

                val topicsCompletedBySubjectByDate =
                    viewModel.getNumberTopicCompletedBySubjectByDate(
                        subject.id, date
                    )

                val numbers = "$topicsCompletedBySubjectByDate/$topicsBySubjectByDate"

                binding.textViewScoreTask.text = numbers


                //Defines the text number of performance
                if (topicsBySubjectByDate > 0) {

                    val perf = viewModel.getPerformanceOfDay(
                        subject.id, date
                    )
                    val stringPerformance = String.format("%.0f", perf) + "%"

                    binding.textViewPerformanceNumber.text = stringPerformance

                    binding.textViewQuestionsNumber.text =
                        viewModel.getNumberOfQuestionsOfDaysBySubject(subject.id, date).toString()

                }
            }

        }


    }

    private object DiffCallback : DiffUtil.ItemCallback<SubjectItem>() {
        override fun areItemsTheSame(oldItem: SubjectItem, newItem: SubjectItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SubjectItem, newItem: SubjectItem): Boolean {
            return (oldItem.title == newItem.title) && (oldItem.isCompleted == newItem.isCompleted)
        }


    }

}