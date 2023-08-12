package com.ramonpsatu.studyorganizer.view.activities


import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.ramonpsatu.studyorganizer.R
import com.ramonpsatu.studyorganizer.databinding.ActivityMainBinding
import com.ramonpsatu.studyorganizer.features.collections.utils.StateHolderObject
import com.ramonpsatu.studyorganizer.features.collections.view.fragments.SettingsFragment
import com.ramonpsatu.studyorganizer.features.collections.viewmodels.SharedPreferencesViewModel
import com.ramonpsatu.studyorganizer.features.collections.viewmodels.SubjectListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class
MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var subjectViewModel: SubjectListViewModel
    private lateinit var preferencesViewModel: SharedPreferencesViewModel
    private lateinit var navController: NavController

    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        subjectViewModel = ViewModelProvider(this)[SubjectListViewModel::class.java]
        preferencesViewModel = ViewModelProvider(this)[SharedPreferencesViewModel::class.java]

        navController = findNavController(R.id.secondary_mobile_nav_graph_II)

        setupBottomNavigation()


        binding.tbnSynchronizeDataLater.setOnClickListener(this)
        binding.tbnSynchronizeDataNow.setOnClickListener(this)
        binding.imageViewOutOfSync.setOnClickListener(this)



    }

    override fun onClick(v: View) {
        when (v.id) {

            binding.tbnSynchronizeDataNow.id -> {


            }

            binding.tbnSynchronizeDataLater.id -> {

                binding.bannerInforOutOfSync.visibility = View.GONE
                binding.imageViewOutOfSync.visibility = View.VISIBLE

                preferencesViewModel.viewModelScope.launch {
                    preferencesViewModel.setShowSyncUI(baseContext, false)

                }
            }

            binding.imageViewOutOfSync.id -> {


                binding.bannerInforOutOfSync.visibility = View.VISIBLE

            }

            else -> {
                return
            }
        }
    }

    override fun onStart() {
        super.onStart()



    }

    override fun onResume() {
        super.onResume()


        when (StateHolderObject.currentNavbarButton) {
            1 -> {
                binding.bottomNavBar.selectedItemId = R.id.subjectFragment
            }

            2 -> {
                binding.bottomNavBar.selectedItemId = R.id.subjectCalendarFragment
            }

            3 -> {

                binding.bottomNavBar.selectedItemId = R.id.homeFragment

            }

            4 -> {
                binding.bottomNavBar.selectedItemId = R.id.printOutFragment

            }

            5 -> {
                binding.bottomNavBar.selectedItemId = R.id.configurationFragment
            }

        }
    }



    private suspend fun handlerErrorState(): String {

        return "Error!"
    }

    private fun setupBottomNavigation() {

        binding.bottomNavBar.itemIconTintList = null
        binding.bottomNavBar.selectedItemId = R.id.homeFragment


       NavigationUI.setupWithNavController(binding.bottomNavBar, navController)


        when (StateHolderObject.currentNavbarButton) {
            1 -> {
                navController.popBackStack()
                navController.navigate(NavDeepLinkRequest.Builder.fromUri("tos-app://com.ramonpesatu.tos/subject_frag".toUri()).build())
            }

            2 -> {
                navController.popBackStack()
                navController.navigate(NavDeepLinkRequest.Builder.fromUri("tos-app://com.ramonpesatu.tos/calendar_screen".toUri()).build())

            }

            3 -> {
                navController.popBackStack()
                navController.navigate(NavDeepLinkRequest.Builder.fromUri("tos-app://com.ramonpesatu.tos/home_screen".toUri()).build())


            }

            4 -> {
                navController.popBackStack()
                navController.navigate(NavDeepLinkRequest.Builder.fromUri("tos-app://com.ramonpesatu.tos/pdf_screen".toUri()).build())


            }

        }

        binding.bottomNavBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.subjectFragment -> {

                    navController.popBackStack()
                    navController.navigate(NavDeepLinkRequest.Builder.fromUri("tos-app://com.ramonpesatu.tos/subject_frag".toUri()).build())

                    StateHolderObject.configurationFragmentFlag = true

                    StateHolderObject.currentNavbarButton = 1
                }


                R.id.subjectCalendarFragment -> {

                    navController.popBackStack()
                    navController.navigate(NavDeepLinkRequest.Builder.fromUri("tos-app://com.ramonpesatu.tos/calendar_screen".toUri()).build())

                    StateHolderObject.configurationFragmentFlag = true

                    StateHolderObject.currentNavbarButton = 2
                }

                R.id.homeFragment -> {
                    navController.popBackStack()
                   navController.navigate(NavDeepLinkRequest.Builder.fromUri("tos-app://com.ramonpesatu.tos/home_screen".toUri()).build())

                    StateHolderObject.configurationFragmentFlag = true
                    StateHolderObject.currentNavbarButton = 3

                }

                R.id.printOutFragment -> {

                    navController.popBackStack()
                    navController.navigate(NavDeepLinkRequest.Builder.fromUri("tos-app://com.ramonpesatu.tos/pdf_screen".toUri()).build())

                    StateHolderObject.configurationFragmentFlag = true


                    StateHolderObject.currentNavbarButton = 4
                }

                R.id.configurationFragment -> {


                    if (StateHolderObject.configurationFragmentFlag && StateHolderObject.configurationFragmentSwitch) {
                        supportFragmentManager.beginTransaction().apply {
                            setCustomAnimations(
                                androidx.appcompat.R.anim.abc_slide_in_bottom,
                                androidx.appcompat.R.anim.abc_slide_out_bottom
                            )
                            setReorderingAllowed(true)
                            add(R.id.secondary_mobile_nav_graph_II, SettingsFragment())
                            addToBackStack(null)
                        }.commit()
                        StateHolderObject.configurationFragmentFlag = false
                        StateHolderObject.configurationFragmentSwitch = false
                    }
                    StateHolderObject.currentNavbarButton = 5
                }
            }
            true
        }
    }


}


