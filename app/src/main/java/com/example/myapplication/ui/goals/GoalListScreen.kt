package com.example.myapplication.ui.goals

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.Goal
import com.example.myapplication.util.TimeColorUtils
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalListScreen(
    viewModel: GoalViewModel,
    onGoalClick: (String) -> Unit,
    onAddGoalClick: () -> Unit
) {
    val goals by viewModel.goals.collectAsState()
    val quote by viewModel.quote.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Goal Breaker") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddGoalClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Goal")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (quote.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text(
                        text = quote,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(goals) { goal ->
                    GoalItem(goal = goal) { onGoalClick(goal.id) }
                }
            }
        }
    }
}

@Composable
fun GoalItem(goal: Goal, onClick: () -> Unit) {
    val urgencyColor = TimeColorUtils.getColorForTimeRemaining(goal.targetDate)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = goal.title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )

                if (goal.isSynced) {
                    Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = "Synced",
                        modifier = Modifier.padding(horizontal = 8.dp).size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = urgencyColor,
                    contentColor = Color.White
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = goal.taskCount.toString(),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
            Text(text = goal.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { goal.progress },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun GoalItemSyncedPreview() {
    MaterialTheme {
        GoalItem(
            goal = Goal(
                title = "Synced Goal",
                description = "This goal is synced to cloud",
                targetDate = System.currentTimeMillis() + 86400000,
                isSynced = true,
                taskCount = 5,
                progress = 0.5f
            ),
            onClick = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun GoalItemNotSyncedPreview() {
    MaterialTheme {
        GoalItem(
            goal = Goal(
                title = "Local Goal",
                description = "This goal is only local",
                targetDate = System.currentTimeMillis() + 86400000,
                isSynced = false,
                taskCount = 2,
                progress = 0.2f
            ),
            onClick = {}
        )
    }
}
