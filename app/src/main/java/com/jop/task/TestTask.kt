package com.jop.task

import android.app.Application
import androidx.room.Room
import com.google.firebase.FirebaseApp
import com.jop.task.data.repository.TodoRepository
import com.jop.task.data.repository.UserRepository
import com.jop.task.data.viewModel.factory.UserViewModelFactory
import com.jop.task.data.viewModel.factory.ManageTaskViewModelFactory
import com.jop.task.data.viewModel.factory.TaskViewModelFactory
import com.jop.task.local.AppData
import com.jop.task.local.database.AppDbLocal
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

class TestTask : Application(), KodeinAware {
    private lateinit var appDbLocal: AppDbLocal

    override val kodein = Kodein.lazy {
        import(androidXModule(this@TestTask))

        bind() from singleton { AppData(instance()) }

        bind() from singleton { UserRepository(appDbLocal.userDao()) }
        bind() from singleton { TodoRepository(appDbLocal.todoDao()) }

        bind() from singleton { UserViewModelFactory(instance(), instance()) }
        bind() from singleton { ManageTaskViewModelFactory(instance(), instance()) }
        bind() from singleton { TaskViewModelFactory(instance(), instance()) }
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        appDbLocal = Room.databaseBuilder(applicationContext, AppDbLocal::class.java, "test_task")
            .fallbackToDestructiveMigration()
            .build()
    }

}