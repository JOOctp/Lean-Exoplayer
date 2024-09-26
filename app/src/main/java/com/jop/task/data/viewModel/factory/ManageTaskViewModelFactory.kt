package com.jop.task.data.viewModel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jop.task.data.repository.TodoRepository
import com.jop.task.data.viewModel.ManageTaskViewModel
import com.jop.task.local.AppData

class ManageTaskViewModelFactory(private val appData: AppData, private val repository: TodoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ManageTaskViewModel(appData, repository) as T
    }
}