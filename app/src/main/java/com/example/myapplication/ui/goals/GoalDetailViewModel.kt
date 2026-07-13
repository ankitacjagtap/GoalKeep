package com.example.myapplication.ui.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Goal
import com.example.myapplication.data.model.Task
import com.example.myapplication.data.repository.GoalRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GoalDetailViewModel(
    private val repository: GoalRepository,
    private val goalId: String
) : ViewModel() {

    val goal: StateFlow<Goal?> = repository.getGoal(goalId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val tasks: StateFlow<List<Task>> = repository.getTasks(goalId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleTaskCompletion(taskId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateTaskCompletion(taskId, isCompleted)
        }
    }

    fun deleteGoal() {
        viewModelScope.launch {
            repository.deleteGoal(goalId)
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }

    fun addTask(title: String, startTime: Long? = null, endTime: Long? = null) {
        viewModelScope.launch {
            repository.addTask(
                Task(
                    goalId = goalId,
                    title = title,
                    startTime = startTime,
                    endTime = endTime
                )
            )
        }
    }
}
