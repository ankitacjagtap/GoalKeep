package com.example.myapplication.data.repository

import android.util.Log
import com.example.myapplication.data.local.GoalDao
import com.example.myapplication.data.model.Goal
import com.example.myapplication.data.model.Task
import com.example.myapplication.notifications.ReminderManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.runBlocking

class FirebaseGoalRepository(
    private val goalDao: GoalDao,
    private val reminderManager: ReminderManager? = null
) : GoalRepository {

    private val database = FirebaseDatabase.getInstance("https://goalkeep-a1cd9-default-rtdb.asia-southeast1.firebasedatabase.app")
    private val goalsRef = database.getReference("goals")
    private val tasksRef = database.getReference("tasks")

    companion object {
        private const val TAG = "FirebaseGoalRepo"
    }

    init {
        Log.d(TAG, "Initializing FirebaseGoalRepository with URL: https://goalkeep-a1cd9-default-rtdb.asia-southeast1.firebasedatabase.app")
        // Simple sync strategy: listen for changes and update local DB
        observeFirebaseChanges()
    }

    private fun observeFirebaseChanges() {
        goalsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "onDataChange: received ${snapshot.childrenCount} goals from Firebase")
                val goals = snapshot.children.mapNotNull { it.getValue(Goal::class.java)?.copy(isSynced = true) }
                // Use a separate scope or worker to sync to local DB in production
                // For this example, we're just setting up the structure
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "onCancelled: Firebase error: ${error.message}", error.toException())
            }
        })
    }

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
        Log.d(TAG, "addGoal: Attempting to save goal ${goal.id} to local DB and Firebase")
        // Save to Local DB
        goalDao.insertGoal(goal)
        // Upload to Firebase
        try {
            goalsRef.child(goal.id).setValue(goal).await()
            Log.d(TAG, "addGoal: Successfully uploaded goal ${goal.id} to Firebase")
            // Update local DB with synced status
            goalDao.insertGoal(goal.copy(isSynced = true))
        } catch (e: Exception) {
            Log.e(TAG, "addGoal: Failed to upload goal ${goal.id} to Firebase: ${e.message}", e)
            // Offline or error - Room handles the "as usual" part
        }
    }

    override suspend fun deleteGoal(goalId: String) {
        goalDao.deleteGoalById(goalId)
        try {
            goalsRef.child(goalId).removeValue().await()
            // Also cleanup tasks in Firebase
            val tasks = goalDao.getTasksForGoal(goalId).first()
            tasks.forEach { tasksRef.child(it.id).removeValue().await() }
        } catch (e: Exception) {}
    }

    override suspend fun addTask(task: Task) {
        Log.d(TAG, "addTask: Attempting to save task ${task.id} to local DB and Firebase")
        goalDao.insertTask(task)
        reminderManager?.scheduleReminder(task)
        try {
            tasksRef.child(task.id).setValue(task).await()
            Log.d(TAG, "addTask: Successfully uploaded task ${task.id} to Firebase")
        } catch (e: Exception) {
            Log.e(TAG, "addTask: Failed to upload task ${task.id} to Firebase: ${e.message}", e)
        }
    }

    override suspend fun deleteTask(taskId: String) {
        goalDao.deleteTask(taskId)
        reminderManager?.cancelReminder(taskId)
        try {
            tasksRef.child(taskId).removeValue().await()
        } catch (e: Exception) {}
    }

    override suspend fun updateTaskCompletion(taskId: String, isCompleted: Boolean) {
        goalDao.updateTaskStatus(taskId, isCompleted)
        if (isCompleted) {
            reminderManager?.cancelReminder(taskId)
        }
        try {
            tasksRef.child(taskId).child("completed").setValue(isCompleted).await()
        } catch (e: Exception) {}
    }

    override suspend fun getMotivationalQuote(): String {
        return "The secret of getting ahead is getting started. – Mark Twain"
    }

    // Example of fetching and syncing (could be called on app start or refresh)
    suspend fun syncWithFirebase() {
        try {
            val goalSnapshot = goalsRef.get().await()
            val firebaseGoals = goalSnapshot.children.mapNotNull { it.getValue(Goal::class.java)?.copy(isSynced = true) }
            firebaseGoals.forEach { goalDao.insertGoal(it) }

            val taskSnapshot = tasksRef.get().await()
            val firebaseTasks = taskSnapshot.children.mapNotNull { it.getValue(Task::class.java) }
            firebaseTasks.forEach { goalDao.insertTask(it) }
        } catch (e: Exception) {}
    }
}
