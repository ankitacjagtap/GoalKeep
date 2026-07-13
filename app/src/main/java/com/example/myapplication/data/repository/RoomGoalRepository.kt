package com.example.myapplication.data.repository

import com.example.myapplication.data.local.GoalDao
import com.example.myapplication.data.model.Goal
import com.example.myapplication.data.model.Task
import com.example.myapplication.notifications.ReminderManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class RoomGoalRepository(
    private val goalDao: GoalDao,
    private val reminderManager: ReminderManager? = null
) : GoalRepository {
    override fun getGoals(): Flow<List<Goal>> = combine(goalDao.getGoals(), goalDao.getAllTasks()) { goals, tasks ->
        goals.map { goal ->
            val goalTasks = tasks.filter { it.goalId == goal.id }
            val progress = if (goalTasks.isEmpty()) 0f else {
                goalTasks.count { it.isCompleted }.toFloat() / goalTasks.size
            }
            goal.copy(progress = progress, taskCount = goalTasks.size)
        }
    }

    override fun getGoal(id: String): Flow<Goal?> = combine(goalDao.getGoalById(id), goalDao.getTasksForGoal(id)) { goal, tasks ->
        goal?.let {
            val progress = if (tasks.isEmpty()) 0f else {
                tasks.count { it.isCompleted }.toFloat() / tasks.size
            }
            it.copy(progress = progress, taskCount = tasks.size)
        }
    }

    override fun getTasks(goalId: String): Flow<List<Task>> = goalDao.getTasksForGoal(goalId)

    override suspend fun addGoal(goal: Goal) {
        goalDao.insertGoal(goal)
    }

    override suspend fun deleteGoal(goalId: String) {
        goalDao.deleteGoalById(goalId)
    }

    override suspend fun addTask(task: Task) {
        goalDao.insertTask(task)
        reminderManager?.scheduleReminder(task)
    }

    override suspend fun deleteTask(taskId: String) {
        goalDao.deleteTask(taskId)
        reminderManager?.cancelReminder(taskId)
    }

    override suspend fun updateTaskCompletion(taskId: String, isCompleted: Boolean) {
        goalDao.updateTaskStatus(taskId, isCompleted)
        if (isCompleted) {
            reminderManager?.cancelReminder(taskId)
        } else {
            // If uncompleted, we might want to re-schedule, but we'd need the full task object.
            // For now, let's focus on scheduling when added.
        }
    }

    override suspend fun getMotivationalQuote(): String {
        return "The secret of getting ahead is getting started. – Mark Twain"
    }
}
