package com.ramonpsatu.studyorganizer.features.collections.view.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ramonpsatu.studyorganizer.features.collections.databinding.SubjectLayoutBinding
import com.ramonpsatu.studyorganizer.features.collections.listeners.ToggleClickListener
import com.ramonpsatu.studyorganizer.features.collections.model.SubjectItem
import com.ramonpsatu.studyorganizer.features.collections.viewmodels.SubjectListViewModel
import kotlinx.coroutines.launch


/**
 * RecyclerView adapter for displaying a list of subjects.
 *
 * The UI is based on the [SubjectLayoutBinding].
 * We use the [SubjectItem] as a model for the binding.
 */

class SubjectListAdapter(
    private val onItemClickEdit: (Int) -> Unit,
    private val onItemClickSearch: (Int) -> Unit,
    private val viewModel: SubjectListViewModel,
    private val toggleClickListener: ToggleClickListener,
) :
    RecyclerView.Adapter<SubjectListAdapter.ViewHolder>() {

    private val asyncListDiffer: AsyncListDiffer<SubjectItem> = AsyncListDiffer(this, DiffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SubjectLayoutBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding, onItemClickEdit, onItemClickSearch, toggleClickListener)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(asyncListDiffer.currentList[position])


    }

    override fun getItemCount(): Int = asyncListDiffer.currentList.size


    fun updateSubjects(subject: List<SubjectItem>) {


        asyncListDiffer.submitList(subject)

    }


    inner class ViewHolder(
        private val binding: SubjectLayoutBinding,
        private val onItemClickEdit: (Int) -> Unit,
        private val onItemClickSearch: (Int) -> Unit,
        private val toggleClickListener: ToggleClickListener,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.icTaskEditCategory.setOnClickListener { onItemClickEdit(adapterPosition) }
            binding.icTaskCheckList.setOnClickListener { onItemClickSearch(adapterPosition) }
        }


        fun bind(subject: SubjectItem) {

            binding.textViewTitle.text = subject.title
            binding.imageViewIcBackgroundSubject.background.setTint(subject.backgroundColor)

            toggle(
                subject,
                binding.imageViewIcBackgroundSubject,
                itemView.context,
                binding.iCheckbox
            )

            viewModel.viewModelScope.launch {
                val topicsBySubject = viewModel.getNumberTopicBySubject(subject.id)
                val topicsCompletedBySubject =
                    viewModel.getNumberTopicCompletedBySubject(subject.id)
                val numbers = "${topicsCompletedBySubject}/${topicsBySubject}"


                binding.textViewScoreTask.text = numbers


                if (topicsBySubject > 0) {
                    val perf = viewModel.getPerformanceBySubject(subject.id)
                    val strFormat = String.format("%.0f", perf) + "%"

                    binding.textViewPerformanceNumber.text = strFormat


                    binding.textViewQuestionsNumber.text =
                        viewModel.getNumberOfQuestionsBySubject(subject.id).toString()
                }


            }

            binding.iCheckbox.setOnClickListener {


                if (subject.isCompleted == 0) {
                    subject.isCompleted = 1

                } else if (subject.isCompleted == 1) {
                    subject.isCompleted = 0

                }
                toggle(
                    subject,
                    binding.imageViewIcBackgroundSubject,
                    itemView.context,
                    binding.iCheckbox
                )

                viewModel.viewModelScope.launch {
                    toggleClickListener.updateToggle(subject.isCompleted, subject.id,adapterPosition)
                }
            }



        }

        private fun toggle(
            subject: SubjectItem,
            view: View,
            context: Context,
            checkbox: ImageView
        ) {

            if (subject.isCompleted == 1) {

                checkbox.setImageDrawable(
                    AppCompatResources.getDrawable(
                        context,
                        com.ramonpsatu.studyorganizer.core.ui.R.drawable.ic_checkbox_completed
                    )
                )

                when (subject.backgroundColor) {
                    //LightGray
                    -2500135 -> {

                        view.background.setTint(
                            ContextCompat.getColor(
                                view.context,
                                com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_light_gray
                            )
                        )

                    }
                    //LightPink
                    -482566 -> {

                        view.background.setTint(
                            ContextCompat.getColor(
                                view.context,
                                com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_pink_dark
                            )
                        )

                    }
                    //CreamOrange
                    -342111 -> {


                        view.background.setTint(
                            ContextCompat.getColor(
                                view.context,
                                com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_orange_dark
                            )
                        )

                    }
                    //RedPink
                    -21589 -> {
                        view.background.setTint(
                            ContextCompat.getColor(
                                view.context,
                                com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_red_pink_dark
                            )
                        )

                    }
                    //LightPurple
                    -4020486 -> {
                        view.background.setTint(
                            ContextCompat.getColor(
                                view.context,
                                com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_purple_dark
                            )
                        )


                    }
                    //LightBlue
                    -5974790 -> {

                        view.background.setTint(
                            ContextCompat.getColor(
                                view.context,
                                com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_blue_dark
                            )
                        )

                    }
                    //LightGreen
                    -4589919 -> {
                        view.background.setTint(
                            ContextCompat.getColor(
                                view.context,
                                com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_green_dark
                            )
                        )

                    }
                    //LightYellow
                    -596352 -> {
                        view.background.setTint(
                            ContextCompat.getColor(
                                view.context,
                                com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_yellow_dark
                            )
                        )


                    }

                }

            } else {
                checkbox.setImageDrawable(
                    AppCompatResources.getDrawable(
                        context,
                        com.ramonpsatu.studyorganizer.core.ui.R.drawable.ic_checkbox
                    )
                )
                when (subject.backgroundColor) {
                    //LightGray
                    -2500135 -> {

                        view.background.setTint(
                            ContextCompat.getColor(
                                view.context,
                                com.ramonpsatu.studyorganizer.core.ui.R.color.light_gray
                            )
                        )

                    }
                    //LightPink
                    -482566 -> {

                        view.background.setTint(
                            ContextCompat.getColor(
                                view.context,
                                com.ramonpsatu.studyorganizer.core.ui.R.color.light_pink
                            )
                        )

                    }
                    //CreamOrange
                    -342111 -> {


                        view.background.setTint(
                            ContextCompat.getColor(
                                view.context,
                                com.ramonpsatu.studyorganizer.core.ui.R.color.cream_orange
                            )
                        )

                    }
                    //RedPink
                    -21589 -> {
                        view.background.setTint(
                            ContextCompat.getColor(
                                view.context,
                                com.ramonpsatu.studyorganizer.core.ui.R.color.red_pink
                            )
                        )

                    }
                    //LightPurple
                    -4020486 -> {
                        view.background.setTint(
                            ContextCompat.getColor(
                                view.context,
                                com.ramonpsatu.studyorganizer.core.ui.R.color.light_purple
                            )
                        )


                    }
                    //LightBlue
                    -5974790 -> {

                        view.background.setTint(
                            ContextCompat.getColor(
                                view.context,
                                com.ramonpsatu.studyorganizer.core.ui.R.color.light_blue
                            )
                        )

                    }
                    //LightGreen
                    -4589919 -> {
                        view.background.setTint(
                            ContextCompat.getColor(
                                view.context,
                                com.ramonpsatu.studyorganizer.core.ui.R.color.light_green
                            )
                        )

                    }
                    //LightYellow
                    -596352 -> {
                        view.background.setTint(
                            ContextCompat.getColor(
                                view.context,
                                com.ramonpsatu.studyorganizer.core.ui.R.color.light_yellow
                            )
                        )


                    }

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