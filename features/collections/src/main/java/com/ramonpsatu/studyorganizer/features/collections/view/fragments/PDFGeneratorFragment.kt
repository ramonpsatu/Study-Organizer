package com.ramonpsatu.studyorganizer.features.collections.view.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build.VERSION
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import com.ramonpsatu.studyorganizer.core.data.utils.toastMessageShort
import com.ramonpsatu.studyorganizer.features.collections.model.SubjectItem
import com.ramonpsatu.studyorganizer.features.collections.model.TopicItem
import com.ramonpsatu.studyorganizer.features.collections.utils.MyCalendar
import com.ramonpsatu.studyorganizer.features.collections.view.adapters.PrintOutListAdapter
import com.ramonpsatu.studyorganizer.features.collections.viewmodels.SharedPreferencesViewModel
import com.ramonpsatu.studyorganizer.features.collections.viewmodels.SubjectListViewModel
import com.ramonpsatu.studyorganizer.features.collections.databinding.FragmentPrintOutBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

@AndroidEntryPoint
class PDFGeneratorFragment : Fragment() {

    private var _binding: FragmentPrintOutBinding? = null
    private val binding get() = _binding!!


    private val pageHeight = 842
    private val pageWidth = 595
    private var subjectPerformance = mutableListOf<Float>()
    private var stringPerformance = mutableListOf<String>()
    private var amountQuestions = mutableListOf<Int>()
    private var amountTopicsOfSubject = mutableListOf<Int>()

    private lateinit var mondayList : List<SubjectItem>
    private lateinit var tuesdayList : List<SubjectItem>
    private lateinit var wednesdayList : List<SubjectItem>
    private lateinit var thursdayList : List<SubjectItem>
    private lateinit var fridayList : List<SubjectItem>
    private lateinit var saturdayList : List<SubjectItem>
    private lateinit var sundayList : List<SubjectItem>

    private lateinit var bitmapLogo: Bitmap
    private lateinit var bitmapLogoScale: Bitmap

    private lateinit var adapter: PrintOutListAdapter
    private var subjectList = emptyList<SubjectItem>()


    private lateinit var permissions : Array<String>

    private var pageNumberGrid = 0
    private var subjectListSize = 0
    private var flagUsedOnGrid = false

    private var indexSubjectItem = 0

    private var allTopicsBySubjectList = emptyList<TopicItem>()

    private var itemSelected = 0
    private var counterRequest = 0


    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + Job())

    private lateinit var subjectListViewModel: SubjectListViewModel
    private lateinit var preferencesViewModel: SharedPreferencesViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        subjectListViewModel = ViewModelProvider(this)[SubjectListViewModel::class.java]
        preferencesViewModel = ViewModelProvider(this)[SharedPreferencesViewModel::class.java]

        checkBuildVersionForPermissions()

        adapter = PrintOutListAdapter(subjectListViewModel)


        bitmapLogo =
            BitmapFactory.decodeResource(resources, com.ramonpsatu.studyorganizer.core.ui.R.drawable.ic_small_size_logo2_app)
        bitmapLogoScale = Bitmap.createScaledBitmap(bitmapLogo, 80, 80, true)

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrintOutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycleViewPrintOut.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
        binding.recycleViewPrintOut.adapter = adapter

        subjectListViewModel.viewModelScope.launch {
            subjectListViewModel.refreshUiState()
            subjectListViewModel.refreshUiStateDaysOfWeek()

        }

        binding.rootAlertDialog.visibility = ViewGroup.INVISIBLE

        uiStateObserve()


        printoutSubjectList()


        printOutGridOfSubjects()


        printOutBlankSubjectGrid()


        printoutSubjectListWhitTopics()

    }


    override fun onResume() {
        super.onResume()

        if (!preferencesViewModel.getPermissionsReadWrite(requireContext())){
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permissions[0]
                ) == PackageManager.PERMISSION_DENIED
            ) {

                binding.rootAlertDialog.visibility = ViewGroup.VISIBLE
            } else {
                binding.rootAlertDialog.visibility = ViewGroup.INVISIBLE

            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkBuildVersionForPermissions(){
        if (VERSION.SDK_INT in 23.. 28){
            permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }else if (VERSION.SDK_INT in 29 ..32){
            permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }else if (VERSION.SDK_INT >= 33){
            preferencesViewModel.setPermissionsReadWrite(requireContext(),true)

        }

        if (!preferencesViewModel.getPermissionsReadWrite(requireContext())){
            handlePermission()
        }
    }
    private fun handlePermission() {


        preferencesViewModel.setPermissionsReadWrite(requireContext(),false)


        val permissionsRequestCallBack = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            var counterTrue = 0

            for (index in result) {

                if (index.value) counterTrue += 1

            }


            if (counterTrue == permissions.size) {

                if (preferencesViewModel.getPermissionsReadWrite(requireContext())) {

                    toastMessageShort(requireContext(),  getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_permissions_granted))

                    preferencesViewModel.setPermissionsReadWrite(requireContext(),true)
                }
            }

            if (result.isEmpty() || counterTrue == 0) {

                preferencesViewModel.setPermissionsReadWrite(requireContext(),false)
            }

        }

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permissions[0]
            ) == PackageManager.PERMISSION_DENIED
        ) {

            if (!preferencesViewModel.getCounterRequest(requireContext())) {

                preferencesViewModel.setCounterRequest(requireContext(),0)
            }

            counterRequest = preferencesViewModel.getCounterRequestReturnInt(requireContext())
            counterRequest += 1

            if (counterRequest in 0..2) {

                permissionsRequestCallBack.launch(permissions)

                preferencesViewModel.setCounterRequest(requireContext(),counterRequest)
            }

        }

        if (counterRequest > 2 && ContextCompat.checkSelfPermission(
                requireContext(),
                permissions[0]
            ) == PackageManager.PERMISSION_DENIED
        ) {

            preferencesViewModel.setCounterRequest(requireContext(),0)
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permissions[0]
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            permissionsRequestCallBack.launch(permissions)
        }
    }

    private fun uiStateObserve() {
        subjectListViewModel.stateOnceAndStream().observe(viewLifecycleOwner) {
            bindUiState(it)

        }

        subjectListViewModel.stateOnceAndDaysOfWeek().observe(viewLifecycleOwner) {
            mondayList = it.subjectItemWeek[0]
            tuesdayList = it.subjectItemWeek[1]
            wednesdayList = it.subjectItemWeek[2]
            thursdayList = it.subjectItemWeek[3]
            fridayList = it.subjectItemWeek[4]
            saturdayList = it.subjectItemWeek[5]
            sundayList = it.subjectItemWeek[6]

        }

    }

    private fun bindUiState(uiState: SubjectListViewModel.UiState) {

        adapter.updateListUiState(uiState.subjectItemList)
        subjectList = uiState.subjectItemList

        if (subjectList.isEmpty()) {
            binding.textViewExhibitionInfor.visibility = View.VISIBLE
        } else {
            binding.textViewExhibitionInfor.visibility = View.INVISIBLE
        }

    }

    private suspend fun setListsOfSubjects() {


        for (index in subjectList.indices step 1) {


            subjectPerformance.add(subjectListViewModel.getPerformanceBySubject(subjectList[index].id))


            amountQuestions.add(subjectListViewModel.getNumberOfQuestionsBySubject(subjectList[index].id))
            amountTopicsOfSubject.add(subjectListViewModel.getNumberTopicBySubject(subjectList[index].id))
            val str = String.format("%.0f", subjectPerformance[index]) + "%"
            stringPerformance.add(str)

        }

    }

    private fun printoutSubjectList() {
        binding.buttonPrintOutSubjectList.setOnClickListener {

            subjectListViewModel.viewModelScope.launch {
                subjectListViewModel.refreshUiState()
                if (subjectList.isNotEmpty()) {
                    setListsOfSubjects()
                    generatePDFOnlySubjects()
                } else {

                    toastMessageShort(requireContext(),  getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_warning_subject_list_empty))

                }


            }


        }
    }

    private fun printoutSubjectListWhitTopics() {

        binding.buttonPrintOutSubjectListWithTopics.setOnClickListener {

            allTopicsBySubjectList = adapter.listToPrintOut


            for (index in subjectList.indices) {

                if (subjectList[index].isSelected == 1) {

                    itemSelected++

                    subjectListViewModel.viewModelScope.launch {
                        subjectListViewModel.updateSubjectSelected(0, subjectList[index].id)
                        subjectListViewModel.refreshUiState()
                    }

                }

            }


            for (index in adapter.turnAllBlank.indices) {

                adapter.turnAllBlank[index].setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        com.ramonpsatu.studyorganizer.core.ui.R.color.white
                    )
                )

            }


            if (allTopicsBySubjectList.isNotEmpty() && itemSelected == 1) {

                scope.launch {
                    indexSubjectItem = subjectList.indexOfFirst { subjectItem ->
                        subjectItem.id == allTopicsBySubjectList[0].subjectId
                    }
                    setListsOfSubjects()
                    generatePDFSubjectPlusAllTopics()

                }

            } else {

                if (itemSelected > 1) {

                toastMessageShort(requireContext(),getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_warning_only_subjects))

                }

                if (allTopicsBySubjectList.isEmpty() && itemSelected == 1) {

                    toastMessageShort(requireContext(),
                        getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_warning_topic_list_empty_for_subject))

                }

                if (allTopicsBySubjectList.isEmpty() && itemSelected == 0 && subjectList.isNotEmpty()) {

                    toastMessageShort(requireContext(),getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_warning_a_subjects))

                }


            }
            itemSelected = 0
        }
    }

    private fun printOutGridOfSubjects() {
        binding.buttonPrintOutGridOfSubjects.setOnClickListener {

            flagUsedOnGrid = true

            scope.launch {
                if (subjectList.isNotEmpty()) {
                    generatePDFWeekSubjects()

                } else {

                    toastMessageShort(requireContext(),getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_warning_grid_empty))
                }
            }

        }
    }

    private fun printOutBlankSubjectGrid() {
        binding.buttonPrintOutBlankSubjectGrid.setOnClickListener {

            val size = binding.editTextRowSize.text.toString()
            flagUsedOnGrid = false
            scope.launch {
                if (size.isNotEmpty() && size != "0" && size != "00") {

                    subjectListSize = binding.editTextRowSize.text.toString().toInt()
                    generatePDFWeekSubjects()

                } else {

                    toastMessageShort(requireContext(),getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_warning_field_cannot_null))

                }
            }
        }
    }


    private suspend fun generatePDFSubjectPlusAllTopics() {


        val pdfDocument = PdfDocument()
        val paint = Paint()
        val title = Paint()

        val subjectListSize = allTopicsBySubjectList.size
        var indexOnThePage = 1
        var indexMin = 0
        var indexMax = 3

        val nbPerformanceWord = getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_performance_word)
        val titleWord = getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_title_word)
        val nbTopicsWord = getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_nb_topics_word)
        val nbQuestionsWord = getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_question_word)
        val tosPhrase = getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_tos_phrase)
        val subjectsWord = getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_subject_word)
        val topicsWord = getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_topics_title_word)
        val dateFormat = DateFormat.getLongDateFormat(requireContext())
        val date = dateFormat.format(MyCalendar.calendarDay.time)

        val pageNumber = ((subjectListSize + 2) / 6) + 1


        for (indexPageNumber in 0 until pageNumber step 1) {
            //create home page information
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()

            //set home page
            val myPage: PdfDocument.Page = pdfDocument.startPage(pageInfo)

            // creating a variable for canvas
            val canvas = myPage.canvas

            //--------page-01-Header------------
            if (indexPageNumber == 0) {
                //--------Header------
                canvas.drawBitmap(bitmapLogoScale, 25f, 40f, paint)

                title.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                title.textSize = 18F
                title.color =
                    ContextCompat.getColor(requireContext(), com.ramonpsatu.studyorganizer.core.ui.R.color.black)
                title.textAlign = Paint.Align.LEFT
                canvas.drawText(tosPhrase, 120f, 60f, title)
                title.textSize = 14F
                canvas.drawText(date, 120f, 85f, title)

                title.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                title.textSize = 24F
                title.color =
                    ContextCompat.getColor(requireContext(), com.ramonpsatu.studyorganizer.core.ui.R.color.black)
                title.textAlign = Paint.Align.CENTER
                canvas.drawText(subjectsWord, 298f, 160f, title)
                canvas.drawText(topicsWord, 298f, 350f, title)

            }

            //-------------Table----------------
            title.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            title.textAlign = Paint.Align.CENTER

            val line = Paint()
            line.strokeWidth = 2f
            var constY = 0
            var constYMinus = 315

            title.color =
                ContextCompat.getColor(requireContext(), com.ramonpsatu.studyorganizer.core.ui.R.color.black)

            for (index in 0 until subjectListSize step 1) {

                if (index == 0) {
                    constY = 0

                } else {
                    constY += 120
                }


                //First page
                if ((indexPageNumber == 0) && (index < 3)) {
                    title.textSize = 16F
                    if (index == 0) {
                        //Subject
                        canvas.drawText(titleWord, 112f, 215f + constY, title)
                        canvas.drawText(nbPerformanceWord, 258f, 215f + constY, title)
                        canvas.drawText(nbQuestionsWord, 380f, 215f + constY, title)
                        canvas.drawText(nbTopicsWord, 508f, 215f + constY, title)

                        title.textSize = 12F
                        canvas.drawText(
                            stringPerformance[indexSubjectItem],
                            257f,
                            265f + constY,
                            title
                        )
                        canvas.drawText(
                            amountQuestions[indexSubjectItem].toString(),
                            382f,
                            265f + constY,
                            title
                        )
                        canvas.drawText(
                            amountTopicsOfSubject[indexSubjectItem].toString(),
                            507f,
                            265f + constY,
                            title
                        )
                        drawTitleOfSubject(
                            title,
                            subjectList[indexSubjectItem].title,
                            canvas,
                            constY,
                            0
                        )

                        canvas.drawLine(25f, 190f + constY, 570f, 190f + constY, line)
                        canvas.drawLine(25f, 220f + constY, 570f, 220f + constY, line)
                        canvas.drawLine(25f, 300f + constY, 570f, 300f + constY, line)

                        canvas.drawLine(26f, 190f + constY, 26f, 300f + constY, line)
                        canvas.drawLine(197f, 190f + constY, 197f, 300f + constY, line)
                        canvas.drawLine(320f, 190f + constY, 320f, 300f + constY, line)
                        canvas.drawLine(445f, 190f + constY, 445f, 300f + constY, line)
                        canvas.drawLine(570f, 190f + constY, 570f, 300f + constY, line)

                        canvas.drawText((indexPageNumber + 1).toString(), 571f, 825f, title)


                    }

                    if (index in 0..2) {
                        //Topics
                        title.textSize = 16F
                        canvas.drawText(titleWord, 112f, 405f + constY, title)
                        canvas.drawText("Description", 298f, 405f + constY, title)
                        canvas.drawText(nbPerformanceWord, 450f, 405f + constY, title)
                        canvas.drawText("N.Quest", 535f, 405f + constY, title)


                        drawDescriptionOfTopics(
                            title, allTopicsBySubjectList[index].description,
                            canvas, 0, constY, 0
                        )
                        //canvas.drawText(allTopicsBySubjectList[index].description,
                        //    298f, 455f + constY, title)

                        title.textSize = 12F
                        canvas.drawText(
                            String.format(
                                "%.0f",
                                allTopicsBySubjectList[index].performance
                            ) + "%", 450f, 455f + constY, title
                        )
                        canvas.drawText(
                            allTopicsBySubjectList[index].amountOfQuestions.toString(),
                            535f,
                            455f + constY,
                            title
                        )
                        drawTitleOfSubject(
                            title,
                            allTopicsBySubjectList[index].title,
                            canvas,
                            constY + 190,
                            0
                        )



                        canvas.drawLine(25f, 380f + constY, 570f, 380f + constY, line)
                        canvas.drawLine(25f, 410f + constY, 570f, 410f + constY, line)
                        canvas.drawLine(25f, 490f + constY, 570f, 490f + constY, line)

                        canvas.drawLine(26f, 380f + constY, 26f, 490f + constY, line)
                        canvas.drawLine(197f, 380f + constY, 197f, 490f + constY, line)
                        canvas.drawLine(400f, 380f + constY, 400f, 490f + constY, line)
                        canvas.drawLine(500f, 380f + constY, 500f, 490f + constY, line)
                        canvas.drawLine(570f, 380f + constY, 570f, 490f + constY, line)

                    }


                    if (index == 2) {
                        indexMin = index + 1
                        indexMax += index + 3
                    }

                }

                //Other pages, 2...3..100...
                if ((indexPageNumber == indexOnThePage) && (index in indexMin..indexMax)) {

                    if (index == indexMin) {
                        constY = 0
                    }


                    title.textSize = 16F
                    canvas.drawText(titleWord, 112f, 85f + constY, title)
                    canvas.drawText("Description", 298f, 85f + constY, title)
                    canvas.drawText(nbPerformanceWord, 450f, 85f + constY, title)
                    canvas.drawText("N.Quest", 535f, 85f + constY, title)

                    if (allTopicsBySubjectList[index].description.length > 180) {
                        constYMinus = 320
                    }


                    title.textSize = 10F
                    drawDescriptionOfTopics(
                        title, allTopicsBySubjectList[index].description,
                        canvas, 0, constY, constYMinus
                    )

                    title.textSize = 12F
                    canvas.drawText(
                        String.format(
                            "%.0f",
                            allTopicsBySubjectList[index].performance
                        ) + "%", 450f, 135f + constY, title
                    )
                    canvas.drawText(
                        allTopicsBySubjectList[index].amountOfQuestions.toString(),
                        535f,
                        135f + constY,
                        title
                    )
                    drawTitleOfSubject(
                        title,
                        allTopicsBySubjectList[index].title,
                        canvas,
                        constY,
                        130
                    )

                    canvas.drawLine(25f, 60f + constY, 570f, 60f + constY, line)
                    canvas.drawLine(25f, 90f + constY, 570f, 90f + constY, line)
                    canvas.drawLine(25f, 170f + constY, 570f, 170f + constY, line)

                    canvas.drawLine(26f, 60f + constY, 26f, 170f + constY, line)
                    canvas.drawLine(197f, 60f + constY, 197f, 170f + constY, line)
                    canvas.drawLine(400f, 60f + constY, 400f, 170f + constY, line)
                    canvas.drawLine(500f, 60f + constY, 500f, 170f + constY, line)
                    canvas.drawLine(570f, 60f + constY, 570f, 170f + constY, line)


                    canvas.drawText((indexPageNumber + 1).toString(), 571f, 825f, title)
                    if (index == (indexMax)) {
                        indexMin = index + 1
                        indexMax = indexMin + 5
                        indexOnThePage += 1
                    }

                }


            }

            pdfDocument.finishPage(myPage)


        }


        val filePDF =
            File(getPathFile("/TOS-PDFs"), getString(com.ramonpsatu.studyorganizer.core.ui.R.string.tos_all_topics_with_the_subject))



        try {

            withContext(Dispatchers.IO) {
                pdfDocument.writeTo(FileOutputStream(filePDF))

            }

            toastMessageShort(requireContext(),getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_warning_pdf_generated_successfully))


        } catch (ex: IOException) {

            ex.printStackTrace()

        }

        pdfDocument.close()

        uploadFileInMediaStore(filePDF, getString(com.ramonpsatu.studyorganizer.core.ui.R.string.tos_all_topics_with_the_subject))

        goToFileDirectory()

    }


    private suspend fun generatePDFWeekSubjects() {


        val bitmapLogo = BitmapFactory.decodeResource(
            resources,
            com.ramonpsatu.studyorganizer.core.ui.R.drawable.ic_small_size_logo_app_40x40
        )
        val bitmapLogoScaled = Bitmap.createScaledBitmap(bitmapLogo, 64, 64, true)

        val pdfDocument = PdfDocument()
        val paint = Paint()
        val title = Paint()


        var indexOnThePage = 1
        var indexMin = 0
        var indexMax = 5

        if (flagUsedOnGrid) {

            if (subjectListSize == 0) {
                if (((mondayList.size >= tuesdayList.size) && (mondayList.size >= wednesdayList.size)
                            && (mondayList.size >= thursdayList.size) && (mondayList.size >= fridayList.size)
                            && (mondayList.size >= saturdayList.size) && (mondayList.size >= sundayList.size))

                ) {
                    subjectListSize = mondayList.size
                }

            }

            if (subjectListSize == 0) {
                if ((tuesdayList.size >= mondayList.size) && (tuesdayList.size >= wednesdayList.size)
                    && (tuesdayList.size >= thursdayList.size) && (tuesdayList.size >= fridayList.size)
                    && (tuesdayList.size >= saturdayList.size) && (tuesdayList.size >= sundayList.size)

                ) {
                    subjectListSize = tuesdayList.size
                }

            }

            if (subjectListSize == 0) {
                if ((wednesdayList.size >= mondayList.size) && (wednesdayList.size >= tuesdayList.size)
                    && (wednesdayList.size >= thursdayList.size) && (wednesdayList.size >= fridayList.size)
                    && (wednesdayList.size >= saturdayList.size) && (wednesdayList.size >= sundayList.size)

                ) {
                    subjectListSize = wednesdayList.size
                }
            }

            if (subjectListSize == 0) {
                if ((thursdayList.size >= mondayList.size) && (thursdayList.size >= tuesdayList.size)
                    && (thursdayList.size >= wednesdayList.size) && (thursdayList.size >= fridayList.size)
                    && (thursdayList.size >= saturdayList.size) && (thursdayList.size >= sundayList.size)

                ) {
                    subjectListSize = thursdayList.size
                }
            }

            if (subjectListSize == 0) {
                if ((fridayList.size >= mondayList.size) && (fridayList.size >= tuesdayList.size)
                    && (fridayList.size >= wednesdayList.size) && (fridayList.size >= thursdayList.size)
                    && (fridayList.size >= saturdayList.size) && (fridayList.size >= sundayList.size)

                ) {
                    subjectListSize = fridayList.size
                }
            }

            if (subjectListSize == 0) {
                if ((saturdayList.size >= mondayList.size) && (saturdayList.size >= tuesdayList.size)
                    && (saturdayList.size >= wednesdayList.size) && (saturdayList.size >= thursdayList.size)
                    && (saturdayList.size >= fridayList.size) && (saturdayList.size >= sundayList.size)

                ) {
                    subjectListSize = saturdayList.size
                }
            }

            if (subjectListSize == 0) {
                if ((sundayList.size >= mondayList.size) && (sundayList.size >= tuesdayList.size)
                    && (sundayList.size >= wednesdayList.size) && (sundayList.size >= thursdayList.size)
                    && (sundayList.size >= fridayList.size) && (sundayList.size >= saturdayList.size)

                ) {
                    subjectListSize = sundayList.size
                }
            }
        }


        var subjectListSizeAid = subjectListSize
        val tosPhrase = getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_tos_phrase)
        val subjectsWord = getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_subjects_word)
        val amountOfPhrase = getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_amount_of_subjects)
        val dateFormat = DateFormat.getLongDateFormat(requireContext())
        val date = dateFormat.format(MyCalendar.calendarDay.time)

        pageNumberGrid = (subjectListSize / 6) + 1


        for (indexPageNumber in 0 until pageNumberGrid step 1) {
            //create home page information
            val pageInfo =
                PdfDocument.PageInfo.Builder(pageHeight, pageWidth, pageNumberGrid).create()

            //set home page
            val myPage: PdfDocument.Page = pdfDocument.startPage(pageInfo)

            // creating a variable for canvas
            val canvas = myPage.canvas

            //--------page-01-Header------------
            if (indexPageNumber == 0) {

                canvas.drawBitmap(bitmapLogoScaled, 25f, 40f, paint)

                title.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                title.textSize = 18F
                title.color =
                    ContextCompat.getColor(requireContext(), com.ramonpsatu.studyorganizer.core.ui.R.color.black)
                title.textAlign = Paint.Align.LEFT
                canvas.drawText(tosPhrase, 104f, 60f, title)

                title.textSize = 14F
                canvas.drawText(date, 104f, 85f, title)
                title.textAlign = Paint.Align.RIGHT
                if (flagUsedOnGrid) {
                    canvas.drawText("$amountOfPhrase ${subjectList.size}", 802f, 145f, title)
                } else {
                    canvas.drawText(amountOfPhrase, 783f, 145f, title)
                }


                title.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                title.textSize = 24F
                title.color =
                    ContextCompat.getColor(requireContext(), com.ramonpsatu.studyorganizer.core.ui.R.color.black)
                title.textAlign = Paint.Align.CENTER
                canvas.drawText(subjectsWord, 421f, 120f, title)

            }

            //-------------Table----------------
            title.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            title.textAlign = Paint.Align.CENTER

            val line = Paint()
            line.strokeWidth = 2f
            var constY = 0
            var constYTitle = 0
            var constYMinus = 0

            title.color =
                ContextCompat.getColor(requireContext(), com.ramonpsatu.studyorganizer.core.ui.R.color.black)

            for (index in 0 until subjectListSize step 1) {

                if (index == 0) {
                    constY = 0
                    constYTitle = 0

                } else {
                    constY += 80
                    constYTitle += 80

                }


                //First page
                if ((indexPageNumber == 0) && (index < 5)) {
                    subjectListSizeAid -= 1


                    if (flagUsedOnGrid) {
                        drawTitlesSubjectsByDayOfWeek(
                            title,
                            canvas,
                            constYTitle,
                            constYMinus,
                            index
                        )
                    }


                    //Draws the lines from left to right of the table
                    canvas.drawLine(91f, 260f + constY, 817f, 260f + constY, line)
                    //Draws the lines from top to bottom of the table
                    canvas.drawLine(26f, 180f + constY, 26f, 260f + constY, line)
                    canvas.drawLine(91f, 180f + constY, 91f, 260f + constY, line)
                    canvas.drawLine(196f, 180f + constY, 196f, 260f + constY, line)
                    canvas.drawLine(303f, 180f + constY, 303f, 260f + constY, line)
                    canvas.drawLine(410f, 180f + constY, 410f, 260f + constY, line)
                    canvas.drawLine(516f, 180f + constY, 516f, 260f + constY, line)
                    canvas.drawLine(623f, 180f + constY, 623f, 260f + constY, line)
                    canvas.drawLine(719f, 180f + constY, 719f, 260f + constY, line)
                    canvas.drawLine(816f, 180f + constY, 816f, 260f + constY, line)


                    //Draw table header
                    if (index == 0) {

                        title.textSize = 16F
                        canvas.drawText(getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_period_word), 58f, 175f, title)
                        canvas.drawText(getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_day_monday_form), 142f, 175f, title)
                        canvas.drawText(
                            getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_day_tuesday_form),
                            249f,
                            175f,
                            title
                        )
                        canvas.drawText(
                            getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_day_wednesday_form),
                            355f,
                            175f,
                            title
                        )
                        canvas.drawText(
                            getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_day_thursday_form),
                            463f,
                            175f,
                            title
                        )
                        canvas.drawText(getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_day_friday_form), 569f, 175f, title)
                        canvas.drawText(
                            getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_day_saturday_form),
                            671f,
                            175f,
                            title
                        )
                        canvas.drawText(getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_day_sunday_form), 768f, 175f, title)
                        canvas.drawLine(25f, 150f, 817f, 150f, line)
                        canvas.drawLine(25f, 180f, 817f, 180f, line)

                        //Draws the lines from top to bottom of the table
                        canvas.drawLine(26f, 150f, 26f, 180f, line)
                        canvas.drawLine(91f, 150f, 91f, 180f, line)
                        canvas.drawLine(196f, 150f, 196f, 180f, line)
                        canvas.drawLine(303f, 150f, 303f, 180f, line)
                        canvas.drawLine(410f, 150f, 410f, 180f, line)
                        canvas.drawLine(516f, 150f, 516f, 180f, line)
                        canvas.drawLine(623f, 150f, 623f, 180f, line)
                        canvas.drawLine(719f, 150f, 719f, 180f, line)
                        canvas.drawLine(816f, 150f, 816f, 180f, line)

                        //Draw page number
                        title.textSize = 12F
                        canvas.drawText((indexPageNumber + 1).toString(), 802f, 38f, title)
                    }

                    if (subjectListSizeAid == 0 || (subjectListSizeAid > 0 && index == 4)) {
                        canvas.drawLine(25f, 260f + constY, 91f, 260f + constY, line)
                    }


                    if (index == 4) {
                        indexMin = index + 1
                        indexMax += index + 1
                    }

                }
                //Other pages, 2...3..100...
                if ((indexPageNumber == indexOnThePage) && (index in indexMin..indexMax)) {

                    subjectListSizeAid -= 1

                    if (index == indexMin) {
                        constY = 0

                        // The First top row of cells
                        canvas.drawLine(25f, 60f + constY, 817f, 60f + constY, line)

                        //Draw page number
                        title.textSize = 12F
                        canvas.drawText((indexPageNumber + 1).toString(), 802f, 38f, title)

                    }


                    //Center the title of the subjects from the second page onwards
                    if (indexOnThePage == 1 && index == indexMin) {
                        constYMinus = 515
                    } else if (indexOnThePage > 1 && index == indexMin) {
                        constYMinus = 515 + (480 * (indexOnThePage - 1))
                    }

                    //draw titles
                    if (flagUsedOnGrid) {
                        drawTitlesSubjectsByDayOfWeek(
                            title,
                            canvas,
                            constYTitle,
                            constYMinus,
                            index
                        )
                    }


                    // Draws the lines from left to right of the table
                    canvas.drawLine(91f, 140f + constY, 817f, 140f + constY, line)
                    //Draws the lines from top to bottom of the table
                    canvas.drawLine(26f, 60f + constY, 26f, 140f + constY, line)
                    canvas.drawLine(91f, 60f + constY, 91f, 140f + constY, line)
                    canvas.drawLine(196f, 60f + constY, 196f, 140f + constY, line)
                    canvas.drawLine(303f, 60f + constY, 303f, 140f + constY, line)
                    canvas.drawLine(410f, 60f + constY, 410f, 140f + constY, line)
                    canvas.drawLine(516f, 60f + constY, 516f, 140f + constY, line)
                    canvas.drawLine(623f, 60f + constY, 623f, 140f + constY, line)
                    canvas.drawLine(719f, 60f + constY, 719f, 140f + constY, line)
                    canvas.drawLine(816f, 60f + constY, 816f, 140f + constY, line)


                    //Bottom row of period cell when page list size is less than 6
                    if (subjectListSizeAid == 0) {
                        canvas.drawLine(25f, 140f + constY, 91f, 140f + constY, line)
                    }

                    //Bottom row of period cell when page list size equals than 6
                    if (subjectListSizeAid == 0 || (subjectListSizeAid > 0 && index == indexMax)) {
                        canvas.drawLine(25f, 140f + constY, 91f, 140f + constY, line)
                    }

                    if (index == (indexMax)) {
                        indexMin = index + 1
                        indexMax = indexMin + 5
                        indexOnThePage += 1
                    }

                }


            }

            pdfDocument.finishPage(myPage)


        }

        val pdfFileName =
            if (flagUsedOnGrid) getString(com.ramonpsatu.studyorganizer.core.ui.R.string.tos_grid_of_subject)
            else getString(com.ramonpsatu.studyorganizer.core.ui.R.string.tos_blank_subject_grid)


        val filePDF = File(getPathFile("/TOS-PDFs"), pdfFileName)

        try {
            withContext(Dispatchers.IO) {
                pdfDocument.writeTo(FileOutputStream(filePDF))

            }

            toastMessageShort(requireContext(),getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_warning_pdf_generated_successfully))


        } catch (ex: IOException) {

            ex.printStackTrace()

        }

        pdfDocument.close()


        uploadFileInMediaStore(filePDF, pdfFileName)




        goToFileDirectory()


    }

    private suspend fun generatePDFOnlySubjects() {

        val pdfDocument = PdfDocument()
        val paint = Paint()
        val title = Paint()

        val subjectListSize = subjectList.size
        var indexOnThePage = 1
        var indexMin = 0
        var indexMax = 5

        val nbPerformanceWord = getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_performance_word)
        val titleWord = getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_title_word)
        val nbTopicsWord = getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_nb_topics_word)
        val nbQuestionsWord = getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_question_word)
        val tosPhrase = getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_tos_phrase)
        val subjectsWord = getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_subjects_word)
        val amountOfPhrase = getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_amount_of_subjects)
        val dateFormat = DateFormat.getLongDateFormat(requireContext())
        val date = dateFormat.format(MyCalendar.calendarDay.time)

        val pageNumber = (subjectListSize / 6) + 1


        for (indexPageNumber in 0 until pageNumber step 1) {
            //create home page information
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()

            //set home page
            val myPage: PdfDocument.Page = pdfDocument.startPage(pageInfo)

            // creating a variable for canvas
            val canvas = myPage.canvas

            //--------page-01-Header------------
            if (indexPageNumber == 0) {
                //--------Header------
                canvas.drawBitmap(bitmapLogoScale, 25f, 40f, paint)

                title.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                title.textSize = 18F
                title.color =
                    ContextCompat.getColor(requireContext(), com.ramonpsatu.studyorganizer.core.ui.R.color.black)
                title.textAlign = Paint.Align.LEFT
                canvas.drawText(tosPhrase, 120f, 60f, title)
                //571
                title.textSize = 14F
                canvas.drawText(date, 120f, 85f, title)
                title.textAlign = Paint.Align.RIGHT
                canvas.drawText("$amountOfPhrase $subjectListSize", 571f, 185f, title)

                title.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                title.textSize = 24F
                title.color =
                    ContextCompat.getColor(requireContext(), com.ramonpsatu.studyorganizer.core.ui.R.color.black)
                title.textAlign = Paint.Align.CENTER
                canvas.drawText(subjectsWord, 298f, 160f, title)

            }

            //-------------Table----------------
            title.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            title.textAlign = Paint.Align.CENTER

            val line = Paint()
            line.strokeWidth = 2f
            var constY = 0

            title.color =
                ContextCompat.getColor(requireContext(), com.ramonpsatu.studyorganizer.core.ui.R.color.black)

            for (index in 0 until subjectListSize step 1) {

                if (index == 0) {
                    constY = 0

                } else {
                    constY += 120
                }


                //First page
                if ((indexPageNumber == 0) && (index < 5)) {
                    title.textSize = 16F
                    canvas.drawText(titleWord, 112f, 215f + constY, title)
                    canvas.drawText(nbPerformanceWord, 258f, 215f + constY, title)
                    canvas.drawText(nbQuestionsWord, 380f, 215f + constY, title)
                    canvas.drawText(nbTopicsWord, 508f, 215f + constY, title)

                    title.textSize = 12F
                    canvas.drawText(stringPerformance[index], 257f, 265f + constY, title)
                    canvas.drawText(amountQuestions[index].toString(), 382f, 265f + constY, title)
                    canvas.drawText(
                        amountTopicsOfSubject[index].toString(),
                        507f,
                        265f + constY,
                        title
                    )
                    drawTitleOfSubject(title, subjectList[index].title, canvas, constY, 0)

                    canvas.drawLine(25f, 190f + constY, 570f, 190f + constY, line)
                    canvas.drawLine(25f, 220f + constY, 570f, 220f + constY, line)
                    canvas.drawLine(25f, 300f + constY, 570f, 300f + constY, line)

                    canvas.drawLine(26f, 190f + constY, 26f, 300f + constY, line)
                    canvas.drawLine(197f, 190f + constY, 197f, 300f + constY, line)
                    canvas.drawLine(320f, 190f + constY, 320f, 300f + constY, line)
                    canvas.drawLine(445f, 190f + constY, 445f, 300f + constY, line)
                    canvas.drawLine(570f, 190f + constY, 570f, 300f + constY, line)

                    canvas.drawText((indexPageNumber + 1).toString(), 571f, 825f, title)

                    if (index == 4) {
                        indexMin = index + 1
                        indexMax += index + 1
                    }

                }
                //Other pages, 2...3..100...
                if ((indexPageNumber == indexOnThePage) && (index in indexMin..indexMax)) {

                    if (index == indexMin) {
                        constY = 0
                    }


                    title.textSize = 16F
                    canvas.drawText(titleWord, 112f, 85f + constY, title)
                    canvas.drawText(nbPerformanceWord, 258f, 85f + constY, title)
                    canvas.drawText(nbQuestionsWord, 380f, 85f + constY, title)
                    canvas.drawText(nbTopicsWord, 508f, 85f + constY, title)

                    title.textSize = 12F
                    canvas.drawText(stringPerformance[index], 257f, 135f + constY, title)
                    canvas.drawText(amountQuestions[index].toString(), 382f, 135f + constY, title)
                    canvas.drawText(
                        amountTopicsOfSubject[index].toString(),
                        507f,
                        135f + constY,
                        title
                    )
                    drawTitleOfSubject(title, subjectList[index].title, canvas, constY, 130)

                    canvas.drawLine(25f, 60f + constY, 570f, 60f + constY, line)
                    canvas.drawLine(25f, 90f + constY, 570f, 90f + constY, line)
                    canvas.drawLine(25f, 170f + constY, 570f, 170f + constY, line)

                    canvas.drawLine(26f, 60f + constY, 26f, 170f + constY, line)
                    canvas.drawLine(197f, 60f + constY, 197f, 170f + constY, line)
                    canvas.drawLine(320f, 60f + constY, 320f, 170f + constY, line)
                    canvas.drawLine(445f, 60f + constY, 445f, 170f + constY, line)
                    canvas.drawLine(570f, 60f + constY, 570f, 170f + constY, line)

                    canvas.drawText((indexPageNumber + 1).toString(), 571f, 825f, title)
                    if (index == (indexMax)) {
                        indexMin = index + 1
                        indexMax = indexMin + 5
                        indexOnThePage += 1
                    }

                }


            }

            pdfDocument.finishPage(myPage)


        }


        val filePDF = File(getPathFile("/TOS-PDFs"), getString(com.ramonpsatu.studyorganizer.core.ui.R.string.tos_list_of_subject))


        try {

            withContext(Dispatchers.IO) {
                pdfDocument.writeTo(FileOutputStream(filePDF))

            }

            toastMessageShort(requireContext(),getString(com.ramonpsatu.studyorganizer.core.ui.R.string.text_warning_pdf_generated_successfully))


        } catch (ex: IOException) {

            ex.printStackTrace()

        }

        pdfDocument.close()

        uploadFileInMediaStore(filePDF, getString(com.ramonpsatu.studyorganizer.core.ui.R.string.tos_list_of_subject))

        goToFileDirectory()

    }

    private fun getPathFile(folderName: String): File {

        var path = File(Environment.getExternalStorageDirectory(), folderName)


        if (VERSION.SDK_INT in 23..28) {


            path = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                folderName
            )
            if (!path.exists()) {
                path.mkdirs()
            }

        }

        if (VERSION.SDK_INT >= 29) {

            path = File(requireContext().getExternalFilesDir(folderName)!!.absolutePath)

            if (!path.exists()) {
                path.mkdirs()
            }
        }

        return path
    }


    @SuppressLint("QueryPermissionsNeeded")
    private fun goToFileDirectory() {

        val intentPdf = Intent()

        intentPdf.action = DownloadManager.ACTION_VIEW_DOWNLOADS
        val activityExists =
            intentPdf.resolveActivityInfo(
                requireActivity().packageManager,
                0
            ) != null

        val alertInforDialogActivityExists = AlertDialog.Builder(requireContext()).apply {


            setTitle(com.ramonpsatu.studyorganizer.core.ui.R.string.text_dialog_activity)
            setMessage(com.ramonpsatu.studyorganizer.core.ui.R.string.text_dialog_activity_exists)
            setPositiveButton(com.ramonpsatu.studyorganizer.core.ui.R.string.text_dialog_activity_browser_button) { _, _ ->
                if (activityExists) {
                    requireActivity().startActivity(Intent.createChooser(intentPdf, "Open Folder"))
                }
            }
            setNegativeButton(com.ramonpsatu.studyorganizer.core.ui.R.string.text_dialog_activity_remain_button) { dialog, _ ->
                dialog.dismiss()
            }

        }

        val alertInforDialogActivityNoExists = AlertDialog.Builder(requireContext()).apply {

            setTitle(com.ramonpsatu.studyorganizer.core.ui.R.string.text_dialog_activity)
            setMessage(com.ramonpsatu.studyorganizer.core.ui.R.string.text_dialog_activity_no_exists)
            setNegativeButton(
                com.ramonpsatu.studyorganizer.core.ui.R.string.text_dialog_activity_understood_button
            ) { dialog, _ ->
                dialog.dismiss()
            }

        }


        if (activityExists) {

            alertInforDialogActivityExists.show()
        } else {

            alertInforDialogActivityNoExists.show()
        }

    }


    private suspend fun uploadFileInMediaStore(filePDF: File, nameFile: String) {

        if (
            VERSION.SDK_INT >= 29
        ) {

            val contentResolver: ContentResolver = requireContext().contentResolver
            val contentValues = ContentValues()
            val mimeType = "application/pdf"


            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, nameFile)
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            contentValues.put(MediaStore.MediaColumns.DATA, System.currentTimeMillis() / 1000)
            contentValues.put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_DOWNLOADS
            )

            val uri =
                contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

            if (uri != null) {

                withContext(Dispatchers.IO) {

                    try {
                        val outputStream: OutputStream = contentResolver.openOutputStream(uri)!!
                        val inputStream: InputStream = FileInputStream(filePDF)

                        val buffer = ByteArray(1024)
                        var bytesRead: Int

                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                        }

                        outputStream.flush()
                        inputStream.close()
                        outputStream.close()

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    contentValues.put(MediaStore.Files.FileColumns.IS_PENDING, 0)

                    if (
                        VERSION.SDK_INT >= 30
                    ) {
                        contentResolver.update(uri, contentValues, null, null)
                    }
                }


            }


        }
    }


    private fun drawDescriptionOfTopics(
        title: Paint,
        str: String,
        canvas: Canvas,
        constXPLus: Int,
        constYPlus: Int,
        constYMinus: Int,

        ) {
        title.textSize = 10F
        if (str.length <= 30) {
            canvas.drawText(
                str.substring(str.indices),
                298f + constXPLus,
                455f + constYPlus - constYMinus,
                title
            )
        }

        if (str.length in 31..60) {
            canvas.drawText(
                str.substring(0..29), 298f + constXPLus, 450f + constYPlus - constYMinus, title
            )
            canvas.drawText(
                str.substring(30 until str.length),
                298f + constXPLus,
                460f + constYPlus - constYMinus,
                title
            )
        }
        if (str.length in 61..90) {
            canvas.drawText(
                str.substring(0..29),
                298f + constXPLus,
                440f + constYPlus - constYMinus,
                title
            )
            canvas.drawText(
                str.substring(30..59),
                298f + constXPLus,
                450f + constYPlus - constYMinus,
                title
            )
            canvas.drawText(
                str.substring(60 until str.length),
                298f + constXPLus,
                460f + constYPlus - constYMinus,
                title
            )
        }
        if (str.length in 91..120) {
            canvas.drawText(
                str.substring(0..29),
                298f + constXPLus,
                435f + constYPlus - constYMinus,
                title
            )
            canvas.drawText(
                str.substring(30..59),
                298f + constXPLus,
                445f + constYPlus - constYMinus,
                title
            )
            canvas.drawText(
                str.substring(60..89),
                298f + constXPLus,
                455f + constYPlus - constYMinus,
                title
            )
            canvas.drawText(
                str.substring(90 until str.length),
                298f + constXPLus,
                465f + constYPlus - constYMinus,
                title
            )
        }

        if (str.length in 121..150) {
            canvas.drawText(
                str.substring(0..29),
                298f + constXPLus,
                430f + constYPlus - constYMinus,
                title
            )
            canvas.drawText(
                str.substring(30..59),
                298f + constXPLus,
                440f + constYPlus - constYMinus,
                title
            )
            canvas.drawText(
                str.substring(60..89),
                298f + constXPLus,
                450f + constYPlus - constYMinus,
                title
            )
            canvas.drawText(
                str.substring(90..119),
                298f + constXPLus,
                460f + constYPlus - constYMinus,
                title
            )
            canvas.drawText(
                str.substring(120 until str.length),
                298f + constXPLus,
                470f + constYPlus - constYMinus,
                title
            )

        }

        if (str.length in 151..180) {
            canvas.drawText(
                str.substring(0..29),
                298f + constXPLus,
                425f + constYPlus - constYMinus,
                title
            )
            canvas.drawText(
                str.substring(30..59),
                298f + constXPLus,
                435f + constYPlus - constYMinus,
                title
            )
            canvas.drawText(
                str.substring(60..89),
                298f + constXPLus,
                445f + constYPlus - constYMinus,
                title
            )
            canvas.drawText(
                str.substring(90..119),
                298f + constXPLus,
                455f + constYPlus - constYMinus,
                title
            )
            canvas.drawText(
                str.substring(120..149),
                298f + constXPLus,
                465f + constYPlus - constYMinus,
                title
            )
            canvas.drawText(
                str.substring(150 until str.length),
                298f + constXPLus,
                475f + constYPlus - constYMinus,
                title
            )

        }
        if (str.length in 181..210) {
            canvas.drawText(
                str.substring(0..29),
                298f + constXPLus,
                423f + constYPlus - constYMinus,
                title
            )
            canvas.drawText(
                str.substring(30..59),
                298f + constXPLus,
                433f + constYPlus - constYMinus,
                title
            )
            canvas.drawText(
                str.substring(60..89),
                298f + constXPLus,
                443f + constYPlus - constYMinus,
                title
            )
            canvas.drawText(
                str.substring(90..119),
                298f + constXPLus,
                453f + constYPlus - constYMinus,
                title
            )
            canvas.drawText(
                str.substring(120..149),
                298f + constXPLus,
                463f + constYPlus - constYMinus,
                title
            )
            canvas.drawText(
                str.substring(150..179),
                298f + constXPLus,
                473f + constYPlus - constYMinus,
                title
            )
            canvas.drawText(
                str.substring(180 until str.length),
                298f + constXPLus,
                483f + constYPlus - constYMinus,
                title
            )

        }

    }

    private fun drawTitlesSubjectsByDayOfWeek(
        title: Paint,
        canvas: Canvas,
        constYTitle: Int,
        constYMinus: Int,
        index: Int
    ) {
        if (mondayList.size - index > 0) {
            drawTitleOfSubjectByWeek(
                title,
                mondayList[index].title,
                canvas, 0,
                constYTitle,
                constYMinus
            )
        }
        if (tuesdayList.size - index > 0) {
            drawTitleOfSubjectByWeek(
                title,
                tuesdayList[index].title,
                canvas, 107,
                constYTitle,
                constYMinus

            )
        }
        if (wednesdayList.size - index > 0) {
            drawTitleOfSubjectByWeek(
                title,
                wednesdayList[index].title,
                canvas, 214,
                constYTitle,
                constYMinus

            )

        }
        if (thursdayList.size - index > 0) {
            drawTitleOfSubjectByWeek(
                title,
                thursdayList[index].title,
                canvas, 321,
                constYTitle,
                constYMinus

            )

        }
        //Friday
        if (fridayList.size - index > 0) {
            drawTitleOfSubjectByWeek(
                title,
                fridayList[index].title,
                canvas, 428,
                constYTitle,
                constYMinus

            )

        }
        //Saturday
        if (saturdayList.size - index > 0) {
            drawTitleOfSubjectByWeek(
                title,
                saturdayList[index].title,
                canvas, 528,
                constYTitle,
                constYMinus

            )

        }
        //Sunday
        if (sundayList.size - index > 0) {
            drawTitleOfSubjectByWeek(
                title,
                sundayList[index].title,
                canvas, 628,
                constYTitle,
                constYMinus

            )

        }


    }

    private fun drawTitleOfSubjectByWeek(
        title: Paint,
        str: String,
        canvas: Canvas,
        constXPLus: Int,
        constYPlus: Int,
        constYMinus: Int,

        ) {
        title.textSize = 9F
        if (str.length <= 15) {
            canvas.drawText(
                str.substring(str.indices),
                142f + constXPLus,
                220f + constYPlus - constYMinus,
                title
            )
        }

        if (str.length in 16..30) {
            canvas.drawText(
                str.substring(0..14), 142f + constXPLus, 220f + constYPlus - constYMinus, title
            )
            canvas.drawText(
                str.substring(15 until str.length),
                142f + constXPLus,
                230f + constYPlus - constYMinus,
                title
            )
        }
        if (str.length in 31..45) {
            canvas.drawText(
                str.substring(0..14),
                142f + constXPLus,
                210f + constYPlus - constYMinus,
                title
            )
            canvas.drawText(
                str.substring(15..30),
                142f + constXPLus,
                220f + constYPlus - constYMinus,
                title
            )
            canvas.drawText(
                str.substring(31 until str.length),
                142f + constXPLus,
                230f + constYPlus - constYMinus,
                title
            )
        }
        if (str.length in 46..60) {
            canvas.drawText(
                str.substring(0..14),
                142f + constXPLus,
                210f + constYPlus - constYMinus,
                title
            )
            canvas.drawText(
                str.substring(15..30),
                142f + constXPLus,
                220f + constYPlus - constYMinus,
                title
            )
            canvas.drawText(
                str.substring(31..45),
                142f + constXPLus,
                230f + constYPlus - constYMinus,
                title
            )
            canvas.drawText(
                str.substring(46 until str.length),
                142f + constXPLus,
                240f + constYPlus - constYMinus,
                title
            )
        }

        if (str.length in 61..76) {
            canvas.drawText(
                str.substring(0..14),
                142f + constXPLus,
                200f + constYPlus - constYMinus,
                title
            )
            canvas.drawText(
                str.substring(15..30),
                142f + constXPLus,
                210f + constYPlus - constYMinus,
                title
            )
            canvas.drawText(
                str.substring(31..45),
                142f + constXPLus,
                220f + constYPlus - constYMinus,
                title
            )
            canvas.drawText(
                str.substring(46..60),
                142f + constXPLus,
                230f + constYPlus - constYMinus,
                title
            )
            canvas.drawText(
                str.substring(61 until str.length),
                142f + constXPLus,
                240f + constYPlus - constYMinus,
                title
            )

        }

    }

    private fun drawTitleOfSubject(
        title: Paint,
        str: String,
        canvas: Canvas,
        constYPlus: Int,
        constYMinus: Int
    ) {


        if (str.length <= 20) {

            canvas.drawText(
                str.substring(str.indices), 112f,
                265f + constYPlus - constYMinus,
                title
            )
        }

        if (str.length in 21..40) {

            canvas.drawText(
                str.substring(0..19), 112f, 260f + constYPlus - constYMinus, title
            )

            canvas.drawText(
                str.substring(20 until str.length), 112f, 275f + constYPlus - constYMinus, title
            )


        }

        if (str.length in 41..60) {
            canvas.drawText(str.substring(0..19), 112f, 250f + constYPlus - constYMinus, title)
            canvas.drawText(str.substring(20..40), 112f, 265f + constYPlus - constYMinus, title)
            canvas.drawText(
                str.substring(41 until str.length),
                112f,
                280f + constYPlus - constYMinus,
                title
            )
        }

        if (str.length in 61..76) {
            canvas.drawText(str.substring(0..19), 112f, 240f + constYPlus - constYMinus, title)
            canvas.drawText(str.substring(20..40), 112f, 255f + constYPlus - constYMinus, title)
            canvas.drawText(str.substring(41..60), 112f, 270f + constYPlus - constYMinus, title)
            canvas.drawText(
                str.substring(61 until str.length),
                112f,
                285f + constYPlus - constYMinus,
                title
            )

        }
    }


}