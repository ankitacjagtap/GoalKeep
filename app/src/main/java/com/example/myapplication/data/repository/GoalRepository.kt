package com.example.myapplication.data.repository

import com.example.myapplication.data.model.Goal
import com.example.myapplication.data.model.Task
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    fun getGoals(): Flow<List<Goal>>
    fun getGoal(id: String): Flow<Goal?>
    fun getTasks(goalId: String): Flow<List<Task>>
    suspend fun addGoal(goal: Goal)
    suspend fun deleteGoal(goalId: String)
    suspend fun addTask(task: Task)
    suspend fun deleteTask(taskId: String)
    suspend fun updateTaskCompletion(taskId: String, isCompleted: Boolean)
    suspend fun getMotivationalQuote(): String
}
