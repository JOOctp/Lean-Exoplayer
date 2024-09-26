package com.jop.task.view.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.firebase.auth.FirebaseAuth
import com.jop.task.R
import com.jop.task.base.ui.BaseActivity
import com.jop.task.data.model.User
import com.jop.task.data.viewModel.TaskViewModel
import com.jop.task.data.viewModel.UserViewModel
import com.jop.task.data.viewModel.factory.TaskViewModelFactory
import com.jop.task.data.viewModel.factory.UserViewModelFactory
import com.jop.task.databinding.ActivityHomeBinding
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class HomeActivity : BaseActivity(), KodeinAware {
    override val kodein by kodein()
    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }
    private lateinit var navController: NavController

    private val taskVMF: TaskViewModelFactory by instance()
    private val userVMF: UserViewModelFactory by instance()
    private lateinit var taskVM: TaskViewModel
    private lateinit var userVM: UserViewModel

    companion object {
        @JvmStatic
        fun newInstance(context: Context?): Intent {
            return Intent(context, HomeActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        navController = findNavController(R.id.nav_host_fragment)

        taskVM = ViewModelProvider(this, taskVMF).get(TaskViewModel::class.java)
        userVM = ViewModelProvider(this, userVMF).get(UserViewModel::class.java)

        binding.apply {
            NavigationUI.setupWithNavController(bottomNav, navController)

            navController.addOnDestinationChangedListener { _, destination, _ ->
                toolbar.toolbar.title = destination.label
            }
        }
    }
}