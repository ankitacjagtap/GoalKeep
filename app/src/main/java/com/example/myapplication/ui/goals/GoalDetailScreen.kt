package com.example.myapplication.ui.goals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.Task
import com.example.myapplication.util.TimeColorUtils
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    viewModel: GoalDetailViewModel,
    onBackClick: () -> Unit
) {
    val goal by viewModel.goal.collectAsState()
    val tasks by viewModel.tasks.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(goal?.title ?: "Goal Detail") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.deleteGoal()
                        onBackClick()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Goal")
                    }
                }
            )
        }
    ) { padding ->
        goal?.let { currentGoal ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text(text = currentGoal.description, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(text = "Tasks", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(tasks) { task ->
                        TaskItem(
                            task = task,
                            onToggle = { viewModel.toggleTaskCompletion(task.id, it) },
                            onDelete = { viewModel.deleteTask(task.id) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                var newTaskTitle by remember { mutableStateOf("") }
                var showTimeRangePicker by remember { mutableStateOf(false) }
                var startTime by remember { mutableStateOf<Pair<Int, Int>?>(null) }
                var endTime by remember { mutableStateOf<Pair<Int, Int>?>(null) }

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newTaskTitle,
                            onValueChange = { newTaskTitle = it },
                            label = { Text("New Task") },
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { showTimeRangePicker = true }) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = "Set Time Window",
                                tint = if (startTime != null) MaterialTheme.colorScheme.primary else LocalContentColor.current
                            )
                        }
                        IconButton(
                            onClick = {
                                if (newTaskTitle.isNotBlank()) {
                                    val startMillis = startTime?.let { timeToMillis(it.first, it.second) }
                                    val endMillis = endTime?.let { timeToMillis(it.first, it.second) }
                                    viewModel.addTask(newTaskTitle, startMillis, endMillis)
                                    newTaskTitle = ""
                                    startTime = null
                                    endTime = null
                                }
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Task")
                        }
                    }
                    if (startTime != null && endTime != null) {
                        Text(
                            text = "Time window: ${formatTime(startTime!!)} - ${formatTime(endTime!!)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                if (showTimeRangePicker) {
                    TimeRangePickerDialog(
                        onDismiss = { showTimeRangePicker = false },
                        onTimeRangeSelected = { start, end ->
                            startTime = start
                            endTime = end
                            showTimeRangePicker = false
                        }
                    )
                }
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeRangePickerDialog(
    onDismiss: () -> Unit,
    onTimeRangeSelected: (Pair<Int, Int>, Pair<Int, Int>) -> Unit
) {
    var selectingStart by remember { mutableStateOf(true) }
    val startTimeState = rememberTimePickerState()
    val endTimeState = rememberTimePickerState()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                if (selectingStart) {
                    selectingStart = false
                } else {
                    onTimeRangeSelected(
                        Pair(startTimeState.hour, startTimeState.minute),
                        Pair(endTimeState.hour, endTimeState.minute)
                    )
                }
            }) {
                Text(if (selectingStart) "Next" else "OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = { Text(if (selectingStart) "Select Start Time" else "Select End Time") },
        text = {
            if (selectingStart) {
                TimePicker(state = startTimeState)
            } else {
                TimePicker(state = endTimeState)
            }
        }
    )
}

@Composable
fun TaskItem(task: Task, onToggle: (Boolean) -> Unit, onDelete: () -> Unit) {
    val backgroundColor = if (task.isCompleted) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    } else {
        TimeColorUtils.getColorForTimeRemaining(task.endTime).copy(alpha = 0.2f)
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = task.isCompleted, onCheckedChange = onToggle)
            Column(modifier = Modifier.weight(1f)) {
                Text(text = task.title, style = MaterialTheme.typography.bodyLarge)
                if (task.startTime != null && task.endTime != null) {
                    Text(
                        text = "${formatMillis(task.startTime)} - ${formatMillis(task.endTime)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete Task",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

private fun timeToMillis(hour: Int, minute: Int): Long {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minute)
    return calendar.timeInMillis
}

private fun formatTime(time: Pair<Int, Int>): String {
    return String.format(Locale.getDefault(), "%02d:%02d", time.first, time.second)
}

private fun formatMillis(millis: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = millis
    return String.format(Locale.getDefault(), "%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
}
