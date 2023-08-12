package com.ramonpsatu.studyorganizer.features.collections.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ramonpsatu.studyorganizer.features.collections.viewmodels.SubjectListViewModel
import com.ramonpsatu.studyorganizer.features.collections.model.SubjectItem
import com.ramonpsatu.studyorganizer.features.collections.model.TopicItem
import com.ramonpsatu.studyorganizer.features.collections.databinding.SubjectCardviewLayoutBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PrintOutListAdapter(
    private val viewModel: SubjectListViewModel
) : RecyclerView.Adapter<PrintOutListAdapter.ViewHolder>() {

    private val asyncDiffList = AsyncListDiffer(this, DiffCallBack)

    var listToPrintOut = listOf<TopicItem>()
    var turnAllBlank = mutableListOf<ViewGroup>()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SubjectCardviewLayoutBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return asyncDiffList.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(asyncDiffList.currentList[position])

    }


    fun updateListUiState(subjectItem: List<SubjectItem>) {
        asyncDiffList.submitList(subjectItem)
    }


    inner class ViewHolder(
        private val binding: SubjectCardviewLayoutBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(subjectItem: SubjectItem) {

            binding.textViewSubjectTitle.text = subjectItem.title
            binding.constraintRoot.background.setTint(subjectItem.backgroundColor)
            binding.coloredRoot.setBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    com.ramonpsatu.studyorganizer.core.ui.R.color.white
                )
            )

            viewModel.viewModelScope.launch {
                val str = ":  ${viewModel.getNumberTopicBySubject(subjectItem.id)}"
                binding.textViewNumberOfTopics.text = str

            }



            if (subjectItem.isSelected == 1) {
                viewModel.viewModelScope.launch(Dispatchers.Main) {
                    viewModel.updateSubjectSelected(0, subjectItem.id)

                }
            }



            binding.constraintRoot.setOnClickListener {

                viewModel.viewModelScope.launch {


                    listToPrintOut = viewModel.fetchAllTopicsBySubject(subjectItem.id)

                    if (asyncDiffList.currentList[adapterPosition].isSelected == 0) {

                        turnAllBlank.add(binding.coloredRoot)

                        viewModel.updateSubjectSelected(1, subjectItem.id)


                        binding.coloredRoot.setBackgroundColor(
                            ContextCompat.getColor(
                                itemView.context,
                                com.ramonpsatu.studyorganizer.core.ui.R.color.bgTextTimer_blue
                            )
                        )

                        viewModel.refreshUiState()


                    } else {

                        viewModel.updateSubjectSelected(0, subjectItem.id)

                        binding.coloredRoot.setBackgroundColor(
                            ContextCompat.getColor(
                                itemView.context,
                                com.ramonpsatu.studyorganizer.core.ui.R.color.white
                            )
                        )
                        viewModel.refreshUiState()
                    }


                }


            }


        }
    }


    private object DiffCallBack : DiffUtil.ItemCallback<SubjectItem>() {
        override fun areItemsTheSame(oldItem: SubjectItem, newItem: SubjectItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SubjectItem, newItem: SubjectItem): Boolean {
            return (oldItem.title == newItem.title) && (oldItem.isCompleted == newItem.isCompleted)
        }


    }


}