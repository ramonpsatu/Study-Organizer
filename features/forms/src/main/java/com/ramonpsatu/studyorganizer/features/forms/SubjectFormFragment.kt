package com.ramonpsatu.studyorganizer.features.forms


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.ramonpsatu.studyorganizer.core.data.utils.toastMessageShort
import com.ramonpsatu.studyorganizer.features.collections.utils.StateHolderObject
import com.ramonpsatu.studyorganizer.features.collections.viewmodels.SubjectListViewModel
import com.ramonpsatu.studyorganizer.features.forms.databinding.FragmentSubjectFormBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.UUID


/**
 * @author Ramon Pedrosa.
 * A [Fragment] that displays a form of the subject.
 * @property backgroundColor sets a background color for the subject item.
 * @property daysOfWeek represents the days of the week the subjects were saved.
 * @property turnOn used to assist in the selection of textViews.
 *
 */
@AndroidEntryPoint
class SubjectFormFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentSubjectFormBinding? = null
    private val binding get() = _binding!!

    private var turnOn: Array<Boolean> = arrayOf(true, true, true, true, true, true, true)
    private var backgroundColor: Int = 0
    private var daysOfWeek: MutableList<Int> = mutableListOf()


    private lateinit var viewModel: SubjectListViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[SubjectListViewModel::class.java]

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSubjectFormBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAllDays()
        changeTextByScreenEditOrNew()
        setOnClickListeners()
        reloadFieldsOfSubject()
        navigation()

    }

    override fun onClick(v: View) {

        switchBackgroundColorDaysOfWeek(v)
        switchBackgroundColorViews(v)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun setAllDays() {
        binding.buttonAllDays.setOnClickListener {

            ifMondaySetDay()
            ifTuesdaySetDay()
            ifWednesdaySetDay()
            ifThursSetDay()
            ifFridaySetDay()
            ifSaturdaySetDay()
            ifSundaySetDay()

        }

    }

    private fun changeTextByScreenEditOrNew() {
        if (StateHolderObject.turn_flag_SaveOrUpdate) {
            binding.textViewSubjectWordExhibition.text =
                getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_new_subject_words)
        } else {
            binding.textViewSubjectWordExhibition.text =
                getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_edit_subject_words)

            binding.buttonNewSubjectSave.text =
                getString(com.ramonpsatu.studyorganizer.core.ui.R.string.contentDescription_save_button)
        }
    }

    /**
     * Used to reload the fields that existing in form screen.
     *
     * This list displays the colors in the int value:
     *  1- (-482566) represents LightPink color.
     *  2- (-342111)  represents CreamOrange color.
     *  3- (-21589)  represents RedPink color.
     *  4- (-4020486) represents LightPurple color.
     *  5- (-5974790)  represents LightBlue color.
     *  6-(-4589919) represents LightGreen color.
     *  7-(-596352) represents LightYellow color.
     *
     *@author Ramon Pedrosa
     */
    private fun reloadFieldsOfSubject() {
        if (!StateHolderObject.turn_flag_SaveOrUpdate) {

            binding.textInputLayoutTitle.setText(StateHolderObject.safeArgsSubjectItem().title)
            reloadTextsFieldsForUpdateSubject()

            val color = StateHolderObject.safeArgsSubjectItem().backgroundColor
            reloadBackgroundViewColorForUpdateSubject(color)

        }

    }


    private fun navigation() {

        binding.imageViewBack.setOnClickListener {
            findNavController().navigate(com.ramonpsatu.studyorganizer.core.ui.R.id.action_subjectFormFragment_to_subjectFragment)
        }

        binding.buttonNewSubjectSave.setOnClickListener {


            onSaveOrUpdate()

        }

    }

    /**
     * Handles saving or updating of the subject.
     */
    private fun onSaveOrUpdate() {

        if (StateHolderObject.turn_flag_SaveOrUpdate) {
            if ((!binding.textInputLayoutTitle.text.isNullOrEmpty())
                && (backgroundColor != 0) && (daysOfWeek.size > 0)
            ) {

                val id = UUID.randomUUID().toString()
                viewModel.viewModelScope.launch {

                    viewModel.addSubject(
                        id,
                        binding.textInputLayoutTitle.text.toString(), 0,
                        backgroundColor, daysOfWeek, 0, viewModel.getSubjectListSize()+1
                    )

                   findNavController().navigate(com.ramonpsatu.studyorganizer.core.ui.R.id.action_subjectFormFragment_to_subjectFragment)
                }
                StateHolderObject.turn_flag_SaveOrUpdate = false
                StateHolderObject.currentNavbarButton = 1
            } else {

                noEmptyFieldsToast()

            }


        } else {

            if ((!binding.textInputLayoutTitle.text.isNullOrEmpty())
                && (backgroundColor != 0) && (daysOfWeek.size > 0)
            ) {

                viewModel.viewModelScope.launch {

                    viewModel.updateSubject(
                        binding.textInputLayoutTitle.text.toString(),
                        backgroundColor, daysOfWeek, StateHolderObject.safeArgsSubjectItem().id
                    )

                    findNavController().navigate(com.ramonpsatu.studyorganizer.core.ui.R.id.action_subjectFormFragment_to_subjectFragment)

                }

            } else {

                noEmptyFieldsToast()

            }

        }
    }


    private fun noEmptyFieldsToast() {

        toastMessageShort(
            requireContext(),
            getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_title_toast_msg_fields_form)
        )
    }



    private fun ifMondaySetDay() {
        if (turnOn[0]) {

            binding.textViewSubjectMonday.background.setTint(
                ContextCompat.getColor(
                    requireContext(),
                    com.ramonpsatu.studyorganizer.core.ui.R.color.colorSecondary_default
                )
            )
            daysOfWeek.add(1)
            turnOn[0] = false

        } else {
            binding.textViewSubjectMonday.background.setTint(
                ContextCompat.getColor(
                    requireContext(),
                    com.ramonpsatu.studyorganizer.core.ui.R.color.white
                )
            )

            daysOfWeek.remove(1)

            turnOn[0] = true
        }

    }

    private fun ifTuesdaySetDay() {
        if (turnOn[1]) {
            binding.textViewSubjectTuesday.background.setTint(
                ContextCompat.getColor(
                    requireContext(),
                    com.ramonpsatu.studyorganizer.core.ui.R.color.colorSecondary_default
                )
            )
            daysOfWeek.add(2)
            turnOn[1] = false
        } else {
            binding.textViewSubjectTuesday.background.setTint(
                ContextCompat.getColor(
                    requireContext(),
                    com.ramonpsatu.studyorganizer.core.ui.R.color.white
                )
            )
            daysOfWeek.remove(2)
            turnOn[1] = true
        }

    }

    private fun ifWednesdaySetDay() {
        if (turnOn[2]) {
            binding.textViewSubjectWednesday.background.setTint(
                ContextCompat.getColor(
                    requireContext(),
                    com.ramonpsatu.studyorganizer.core.ui.R.color.colorSecondary_default
                )
            )
            daysOfWeek.add(3)
            turnOn[2] = false

        } else {
            binding.textViewSubjectWednesday.background.setTint(
                ContextCompat.getColor(
                    requireContext(),
                    com.ramonpsatu.studyorganizer.core.ui.R.color.white
                )
            )
            daysOfWeek.remove(3)
            turnOn[2] = true
        }


    }

    private fun ifThursSetDay() {
        if (turnOn[3]) {
            binding.textViewSubjectThursday.background.setTint(
                ContextCompat.getColor(
                    requireContext(),
                    com.ramonpsatu.studyorganizer.core.ui.R.color.colorSecondary_default
                )
            )

            daysOfWeek.add(4)
            turnOn[3] = false

        } else {
            binding.textViewSubjectThursday.background.setTint(
                ContextCompat.getColor(
                    requireContext(),
                    com.ramonpsatu.studyorganizer.core.ui.R.color.white
                )
            )
            daysOfWeek.remove(4)
            turnOn[3] = true
        }

    }

    private fun ifFridaySetDay() {
        if (turnOn[4]) {
            binding.textViewSubjectFriday.background.setTint(
                ContextCompat.getColor(
                    requireContext(),
                    com.ramonpsatu.studyorganizer.core.ui.R.color.colorSecondary_default
                )
            )
            daysOfWeek.add(5)
            turnOn[4] = false

        } else {
            binding.textViewSubjectFriday.background.setTint(
                ContextCompat.getColor(
                    requireContext(),
                    com.ramonpsatu.studyorganizer.core.ui.R.color.white
                )
            )
            daysOfWeek.remove(5)
            turnOn[4] = true
        }

    }

    private fun ifSaturdaySetDay() {
        if (turnOn[5]) {
            binding.textViewSubjectSaturday.background.setTint(
                ContextCompat.getColor(
                    requireContext(),
                    com.ramonpsatu.studyorganizer.core.ui.R.color.colorSecondary_default
                )
            )
            daysOfWeek.add(6)
            turnOn[5] = false

        } else {
            binding.textViewSubjectSaturday.background.setTint(
                ContextCompat.getColor(
                    requireContext(),
                    com.ramonpsatu.studyorganizer.core.ui.R.color.white
                )
            )
            daysOfWeek.remove(6)
            turnOn[5] = true
        }

    }

    private fun ifSundaySetDay() {
        if (turnOn[6]) {
            binding.textViewSubjectSunday.background.setTint(
                ContextCompat.getColor(
                    requireContext(),
                    com.ramonpsatu.studyorganizer.core.ui.R.color.colorSecondary_default
                )
            )
            daysOfWeek.add(7)
            turnOn[6] = false

        } else {
            binding.textViewSubjectSunday.background.setTint(
                ContextCompat.getColor(
                    requireContext(),
                    com.ramonpsatu.studyorganizer.core.ui.R.color.white
                )
            )

            daysOfWeek.remove(7)

            turnOn[6] = true
        }


    }

    /**
     * Returns an int value that represents the color in the database.
     */
    private fun setBackgroundColor(v: View): Int {
        var backgroundColor = 0

        when (v.id) {

            R.id.view_color_light_gray-> {
                backgroundColor =
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.light_gray
                    )


            }

            R.id.view_color_lightPink -> {
                backgroundColor =
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.light_pink
                    )


            }

            R.id.view_color_creamOrange -> {
                backgroundColor =
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.cream_orange
                    )


            }

            R.id.view_color_redPink -> {
                backgroundColor =
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.red_pink
                    )


            }

            R.id.view_color_lightPurple -> {

                backgroundColor =
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.light_purple
                    )


            }

            R.id.view_color_lightBlue -> {

                backgroundColor =
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.light_blue
                    )


            }

            R.id.view_color_lightGreen -> {

                backgroundColor =
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.light_green
                    )


            }

            R.id.view_color_lightYellow -> {
                backgroundColor =
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.light_yellow
                    )


            }
        }

        return backgroundColor
    }

    private fun setOnClickListeners() {
        binding.textViewSubjectMonday.setOnClickListener(this)
        binding.textViewSubjectTuesday.setOnClickListener(this)
        binding.textViewSubjectWednesday.setOnClickListener(this)
        binding.textViewSubjectThursday.setOnClickListener(this)
        binding.textViewSubjectFriday.setOnClickListener(this)
        binding.textViewSubjectSaturday.setOnClickListener(this)
        binding.textViewSubjectSunday.setOnClickListener(this)
        binding.viewColorLightPink.setOnClickListener(this)
        binding.viewColorCreamOrange.setOnClickListener(this)
        binding.viewColorRedPink.setOnClickListener(this)
        binding.viewColorLightPurple.setOnClickListener(this)
        binding.viewColorLightBlue.setOnClickListener(this)
        binding.viewColorLightGreen.setOnClickListener(this)
        binding.viewColorLightYellow.setOnClickListener(this)
        binding.viewColorLightGray.setOnClickListener(this)


    }

    /**
     * Choose a color,switch between background colors.
     */
    private fun switchBackgroundColorViews(v: View) {

        when (v.id) {
            R.id.view_color_light_gray -> {

                this.backgroundColor = setBackgroundColor(v)


                binding.viewColorLightGray.background.setTint(ContextCompat.getColor(
                    requireContext(),
                    com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_light_gray
                ))

                binding.viewColorLightPink.background.setTint(ContextCompat.getColor(
                    requireContext(),
                    com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_pink
                ))

                binding.viewColorCreamOrange.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_orange
                    )
                )
                binding.viewColorRedPink.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_red_pink
                    )
                )
                binding.viewColorLightPurple.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_purple
                    )
                )
                binding.viewColorLightBlue.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_blue
                    )
                )
                binding.viewColorLightGreen.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_green
                    )
                )
                binding.viewColorLightYellow.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_yellow
                    )
                )

            }

            R.id.view_color_lightPink -> {

                this.backgroundColor = setBackgroundColor(v)


                binding.viewColorLightPink.background.setTint(backgroundColor)

                binding.viewColorLightGray.background.setTint(ContextCompat.getColor(
                    requireContext(),
                    com.ramonpsatu.studyorganizer.core.ui.R.color.light_gray
                ))

                binding.viewColorCreamOrange.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_orange
                    )
                )
                binding.viewColorRedPink.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_red_pink
                    )
                )
                binding.viewColorLightPurple.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_purple
                    )
                )
                binding.viewColorLightBlue.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_blue
                    )
                )
                binding.viewColorLightGreen.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_green
                    )
                )
                binding.viewColorLightYellow.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_yellow
                    )
                )

            }

            R.id.view_color_creamOrange -> {
                this.backgroundColor = setBackgroundColor(v)

                binding.viewColorLightGray.background.setTint(ContextCompat.getColor(
                    requireContext(),
                    com.ramonpsatu.studyorganizer.core.ui.R.color.light_gray
                ))

                binding.viewColorCreamOrange.background.setTint(
                    backgroundColor
                )

                binding.viewColorLightPink.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_pink
                    )
                )

                binding.viewColorRedPink.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_red_pink
                    )
                )
                binding.viewColorLightPurple.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_purple
                    )
                )
                binding.viewColorLightBlue.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_blue
                    )
                )
                binding.viewColorLightGreen.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_green
                    )
                )
                binding.viewColorLightYellow.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_yellow
                    )
                )


            }

            R.id.view_color_redPink -> {
                this.backgroundColor = setBackgroundColor(v)

                binding.viewColorLightGray.background.setTint(ContextCompat.getColor(
                    requireContext(),
                    com.ramonpsatu.studyorganizer.core.ui.R.color.light_gray
                ))

                binding.viewColorRedPink.background.setTint(
                    backgroundColor
                )

                binding.viewColorCreamOrange.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_orange
                    )
                )

                binding.viewColorLightPink.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_pink
                    )
                )

                binding.viewColorLightPurple.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_purple
                    )
                )
                binding.viewColorLightBlue.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_blue
                    )
                )
                binding.viewColorLightGreen.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_green
                    )
                )
                binding.viewColorLightYellow.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_yellow
                    )
                )
            }

            R.id.view_color_lightPurple -> {
                this.backgroundColor = setBackgroundColor(v)

                binding.viewColorLightGray.background.setTint(ContextCompat.getColor(
                    requireContext(),
                    com.ramonpsatu.studyorganizer.core.ui.R.color.light_gray
                ))

                binding.viewColorLightPurple.background.setTint(
                    backgroundColor
                )

                binding.viewColorRedPink.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_red_pink
                    )
                )

                binding.viewColorCreamOrange.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_orange
                    )
                )

                binding.viewColorLightPink.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_pink
                    )
                )

                binding.viewColorLightBlue.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_blue
                    )
                )
                binding.viewColorLightGreen.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_green
                    )
                )
                binding.viewColorLightYellow.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_yellow
                    )
                )

            }

            R.id.view_color_lightBlue -> {

                binding.viewColorLightGray.background.setTint(ContextCompat.getColor(
                    requireContext(),
                    com.ramonpsatu.studyorganizer.core.ui.R.color.light_gray
                ))

                this.backgroundColor = setBackgroundColor(v)
                binding.viewColorLightBlue.background.setTint(
                    backgroundColor
                )

                binding.viewColorRedPink.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_red_pink
                    )
                )

                binding.viewColorCreamOrange.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_orange
                    )
                )

                binding.viewColorLightPink.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_pink
                    )
                )

                binding.viewColorLightPurple.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_purple
                    )
                )
                binding.viewColorLightGreen.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_green
                    )
                )
                binding.viewColorLightYellow.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_yellow
                    )
                )

            }

            R.id.view_color_lightGreen -> {

                binding.viewColorLightGray.background.setTint(ContextCompat.getColor(
                    requireContext(),
                    com.ramonpsatu.studyorganizer.core.ui.R.color.light_gray
                ))

                this.backgroundColor = setBackgroundColor(v)
                binding.viewColorLightGreen.background.setTint(
                    backgroundColor
                )

                binding.viewColorLightBlue.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_blue
                    )
                )

                binding.viewColorRedPink.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_red_pink
                    )
                )

                binding.viewColorCreamOrange.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_orange
                    )
                )

                binding.viewColorLightPink.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_pink
                    )
                )

                binding.viewColorLightPurple.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_purple
                    )
                )

                binding.viewColorLightYellow.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_yellow
                    )
                )

            }

            R.id.view_color_lightYellow -> {
                this.backgroundColor = setBackgroundColor(v)

                binding.viewColorLightGray.background.setTint(ContextCompat.getColor(
                    requireContext(),
                    com.ramonpsatu.studyorganizer.core.ui.R.color.light_gray
                ))

                binding.viewColorLightYellow.background.setTint(
                    backgroundColor
                )


                binding.viewColorLightGreen.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_green
                    )
                )

                binding.viewColorLightBlue.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_blue
                    )
                )

                binding.viewColorRedPink.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_red_pink
                    )
                )

                binding.viewColorCreamOrange.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_orange
                    )
                )

                binding.viewColorLightPink.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_pink
                    )
                )

                binding.viewColorLightPurple.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_purple
                    )
                )

            }

        }


    }

    /**
     * Choose the days of the week,alternate between days of the week.
     */
    private fun switchBackgroundColorDaysOfWeek(v: View) {

        when (v.id) {

            R.id.textView_subject_monday -> {

                ifMondaySetDay()
            }

            R.id.textView_subject_tuesday -> {
                ifTuesdaySetDay()
            }

            R.id.textView_subject_wednesday -> {
                ifWednesdaySetDay()
            }

            R.id.textView_subject_thursday -> {
                ifThursSetDay()
            }

            R.id.textView_subject_friday -> {
                ifFridaySetDay()
            }

            R.id.textView_subject_saturday -> {
                ifSaturdaySetDay()
            }

            R.id.textView_subject_sunday -> {
                ifSundaySetDay()

            }

        }


    }

    /**
     * Reload the days of the week that subjects were saved.
     */
    private fun reloadTextsFieldsForUpdateSubject() {

        for (i in 0..StateHolderObject.safeArgsSubjectItem().daysOfWeek.size step 1) {

            for (e in StateHolderObject.safeArgsSubjectItem().daysOfWeek) {

                when (e) {

                    1 -> {
                        if (turnOn[0]) {

                            ifMondaySetDay()
                        }
                    }

                    2 -> {
                        if (turnOn[1]) {
                            ifTuesdaySetDay()
                        }


                    }

                    3 -> {
                        if (turnOn[2]) {
                            ifWednesdaySetDay()
                        }


                    }

                    4 -> {
                        if (turnOn[3]) {
                            ifThursSetDay()
                        }


                    }

                    5 -> {
                        if (turnOn[4]) {
                            ifFridaySetDay()
                        }


                    }

                    6 -> {
                        if (turnOn[5]) {
                            ifSaturdaySetDay()
                        }

                    }

                    7 -> {
                        if (turnOn[6]) {
                            ifSundaySetDay()
                        }


                    }


                }


            }
        }


    }

    private fun reloadBackgroundViewColorForUpdateSubject(color: Int) {

        when (color) {
            //LightGray
            -2500135 -> {
                this.backgroundColor = color


                binding.viewColorLightGray.background.setTint(ContextCompat.getColor(
                    requireContext(),
                    com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_light_gray
                ))

                binding.viewColorLightPink.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_pink
                    )
                )

                binding.viewColorCreamOrange.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_orange
                    )
                )
                binding.viewColorRedPink.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_red_pink
                    )
                )
                binding.viewColorLightPurple.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_purple
                    )
                )
                binding.viewColorLightBlue.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_blue
                    )
                )
                binding.viewColorLightGreen.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_green
                    )
                )
                binding.viewColorLightYellow.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_yellow
                    )
                )

            }
            //LightPink
            -482566 -> {
                this.backgroundColor = color

                binding.viewColorLightGray.background.setTint(ContextCompat.getColor(
                    requireContext(),
                    com.ramonpsatu.studyorganizer.core.ui.R.color.light_gray
                ))


                binding.viewColorLightPink.background.setTint(backgroundColor)


                binding.viewColorCreamOrange.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_orange
                    )
                )
                binding.viewColorRedPink.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_red_pink
                    )
                )
                binding.viewColorLightPurple.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_purple
                    )
                )
                binding.viewColorLightBlue.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_blue
                    )
                )
                binding.viewColorLightGreen.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_green
                    )
                )
                binding.viewColorLightYellow.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_yellow
                    )
                )

            }
            //CreamOrange
            -342111 -> {
                this.backgroundColor = color
                binding.viewColorCreamOrange.background.setTint(
                    backgroundColor
                )
                binding.viewColorLightGray.background.setTint(ContextCompat.getColor(
                    requireContext(),
                    com.ramonpsatu.studyorganizer.core.ui.R.color.light_gray
                ))

                binding.viewColorLightPink.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_pink
                    )
                )

                binding.viewColorRedPink.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_red_pink
                    )
                )
                binding.viewColorLightPurple.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_purple
                    )
                )
                binding.viewColorLightBlue.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_blue
                    )
                )
                binding.viewColorLightGreen.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_green
                    )
                )
                binding.viewColorLightYellow.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_yellow
                    )
                )


            }
            //RedPink
            -21589 -> {
                this.backgroundColor = color
                binding.viewColorRedPink.background.setTint(
                    backgroundColor
                )
                binding.viewColorLightGray.background.setTint(ContextCompat.getColor(
                    requireContext(),
                    com.ramonpsatu.studyorganizer.core.ui.R.color.light_gray
                ))

                binding.viewColorCreamOrange.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_orange
                    )
                )

                binding.viewColorLightPink.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_pink
                    )
                )

                binding.viewColorLightPurple.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_purple
                    )
                )
                binding.viewColorLightBlue.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_blue
                    )
                )
                binding.viewColorLightGreen.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_green
                    )
                )
                binding.viewColorLightYellow.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_yellow
                    )
                )
            }
            //LightPurple
            -4020486 -> {
                this.backgroundColor = color
                binding.viewColorLightPurple.background.setTint(
                    backgroundColor
                )

                binding.viewColorLightGray.background.setTint(ContextCompat.getColor(
                    requireContext(),
                    com.ramonpsatu.studyorganizer.core.ui.R.color.light_gray
                ))

                binding.viewColorRedPink.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_red_pink
                    )
                )

                binding.viewColorCreamOrange.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_orange
                    )
                )

                binding.viewColorLightPink.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_pink
                    )
                )

                binding.viewColorLightBlue.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_blue
                    )
                )
                binding.viewColorLightGreen.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_green
                    )
                )
                binding.viewColorLightYellow.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_yellow
                    )
                )

            }
            //LightBlue
            -5974790 -> {
                this.backgroundColor = color
                binding.viewColorLightBlue.background.setTint(
                    backgroundColor
                )

                binding.viewColorLightGray.background.setTint(ContextCompat.getColor(
                    requireContext(),
                    com.ramonpsatu.studyorganizer.core.ui.R.color.light_gray
                ))

                binding.viewColorRedPink.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_red_pink
                    )
                )

                binding.viewColorCreamOrange.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_orange
                    )
                )

                binding.viewColorLightPink.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_pink
                    )
                )

                binding.viewColorLightPurple.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_purple
                    )
                )
                binding.viewColorLightGreen.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_green
                    )
                )
                binding.viewColorLightYellow.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_yellow
                    )
                )

            }
            //LightGreen
            -4589919 -> {
                this.backgroundColor = color
                binding.viewColorLightGreen.background.setTint(
                    backgroundColor
                )

                binding.viewColorLightGray.background.setTint(ContextCompat.getColor(
                    requireContext(),
                    com.ramonpsatu.studyorganizer.core.ui.R.color.light_gray
                ))

                binding.viewColorLightBlue.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_blue
                    )
                )

                binding.viewColorRedPink.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_red_pink
                    )
                )

                binding.viewColorCreamOrange.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_orange
                    )
                )

                binding.viewColorLightPink.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_pink
                    )
                )

                binding.viewColorLightPurple.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_purple
                    )
                )

                binding.viewColorLightYellow.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_yellow
                    )
                )

            }
            //LightYellow
            -596352 -> {
                this.backgroundColor = color
                binding.viewColorLightYellow.background.setTint(
                    backgroundColor
                )

                binding.viewColorLightGray.background.setTint(ContextCompat.getColor(
                    requireContext(),
                    com.ramonpsatu.studyorganizer.core.ui.R.color.light_gray
                ))


                binding.viewColorLightGreen.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_green
                    )
                )

                binding.viewColorLightBlue.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_blue
                    )
                )

                binding.viewColorRedPink.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_red_pink
                    )
                )

                binding.viewColorCreamOrange.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_orange
                    )
                )

                binding.viewColorLightPink.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_pink
                    )
                )

                binding.viewColorLightPurple.background.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.bgColor_colorView_purple
                    )
                )

            }

        }

    }

}