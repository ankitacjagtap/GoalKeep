package com.example.myapplication.ui.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Goal
import com.example.myapplication.data.repository.GoalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GoalViewModel(private val repository: GoalRepository) : ViewModel() {

    private val _quote = MutableStateFlow("")
    val quote: StateFlow<String> = _quote.asStateFlow()

    val goals: StateFlow<List<Goal>> = repository.getGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        fetchQuote()
    }

    private fun fetchQuote() {
        viewModelScope.launch {
            _quote.value = repository.getMotivationalQuote()
        }
    }

    fun addGoal(title: String, description: String, targetDate: Long) {
        viewModelScope.launch {
            repository.addGoal(Goal(title = title, description = description, targetDate = targetDate))
        }
    }
}
