package com.jop.task.data.viewModel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jop.task.data.repository.UserRepository
import com.jop.task.data.viewModel.UserViewModel
import com.jop.task.local.AppData

class UserViewModelFactory(private val appData: AppData, private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserViewModel(appData, repository) as T
    }
}