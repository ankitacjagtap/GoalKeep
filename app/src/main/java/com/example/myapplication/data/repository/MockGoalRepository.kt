package com.example.myapplication.data.repository

import com.example.myapplication.data.model.Goal
import com.example.myapplication.data.model.Task
import com.example.myapplication.notifications.ReminderManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

import kotlinx.coroutines.flow.combine

class MockGoalRepository(private val reminderManager: ReminderManager? = null) : GoalRepository {
    private val goals = MutableStateFlow(
        listOf(
            Goal(title = "Learn Jetpack Compose", description = "Master modern Android UI", targetDate = System.currentTimeMillis() + (86400000 * 30)),
            Goal(title = "Build a Productivity App", description = "Create Goal Breaker", targetDate = System.currentTimeMillis() + (86400000 * 15))
        )
    )

    private val tasks = MutableStateFlow(
        listOf(
            Task(goalId = goals.value[0].id, title = "Read documentation"),
            Task(goalId = goals.value[0].id, title = "Complete codelabs"),
            Task(goalId = goals.value[1].id, title = "Design UI"),
            Task(goalId = goals.value[1].id, title = "Implement Room")
        )
    )

    override fun getGoals(): Flow<List<Goal>> = combine(goals, tasks) { gList, tList ->
        gList.map { goal ->
            val goalTasks = tList.filter { it.goalId == goal.id }
            val progress = if (goalTasks.isEmpty()) 0f else {
                goalTasks.count { it.isCompleted }.toFloat() / goalTasks.size
            }
            goal.copy(progress = progress, taskCount = goalTasks.size)
        }
    }

    override fun getGoal(id: String): Flow<Goal?> = combine(goals, tasks) { gList, tList ->
        gList.find { it.id == id }?.let { goal ->
            val goalTasks = tList.filter { it.goalId == goal.id }
            val progress = if (goalTasks.isEmpty()) 0f else {
                goalTasks.count { it.isCompleted }.toFloat() / goalTasks.size
            }
            goal.copy(progress = progress, taskCount = goalTasks.size)
        }
    }

    override fun getTasks(goalId: String): Flow<List<Task>> = tasks.map { it.filter { t -> t.goalId == goalId } }

    override suspend fun addGoal(goal: Goal) {
        goals.update { it + goal }
    }

    override suspend fun deleteGoal(goalId: String) {
        goals.update { list -> list.filter { it.id != goalId } }
        tasks.update { list -> list.filter { it.goalId != goalId } }
    }

    override suspend fun addTask(task: Task) {
        tasks.update { it + task }
        reminderManager?.scheduleReminder(task)
    }

    override suspend fun deleteTask(taskId: String) {
        tasks.update { list ->
            list.filter { it.id != taskId }
        }
        reminderManager?.cancelReminder(taskId)
    }

    override suspend fun updateTaskCompletion(taskId: String, isCompleted: Boolean) {
        tasks.update { list ->
            list.map { if (it.id == taskId) it.copy(isCompleted = isCompleted) else it }
        }
        if (isCompleted) {
            reminderManager?.cancelReminder(taskId)
        }
    }

    override suspend fun getMotivationalQuote(): String {
        return "The secret of getting ahead is getting started. – Mark Twain"
    }
}
