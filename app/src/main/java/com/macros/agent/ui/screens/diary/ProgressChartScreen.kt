package com.macros.agent.ui.screens.diary

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.macros.agent.ui.theme.CaloriesColor
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.composed.plus
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressChartScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProgressChartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val daysRange by viewModel.daysRange.collectAsState()

    val chartEntryModelProducer = remember(uiState.history) {
        ChartEntryModelProducer(uiState.history.mapIndexed { index, item ->
            entryOf(index.toFloat(), item.totalCalories)
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calorie Progress") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Last $daysRange Days",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
                }
            } else if (uiState.history.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        "No data available for this range",
                        modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
                    )
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Chart(
                        chart = lineChart(),
                        model = chartEntryModelProducer.getModel()!!,
                        startAxis = rememberStartAxis(),
                        bottomAxis = rememberBottomAxis(
                            valueFormatter = AxisValueFormatter { value, _ ->
                                uiState.history.getOrNull(value.toInt())?.date?.takeLast(5) ?: ""
                            }
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                FilterChip(selected = daysRange == 7, onClick = { viewModel.updateRange(7) }, label = { Text("7D") })
                FilterChip(selected = daysRange == 14, onClick = { viewModel.updateRange(14) }, label = { Text("14D") })
                FilterChip(selected = daysRange == 30, onClick = { viewModel.updateRange(30) }, label = { Text("30D") })
            }
        }
    }
}
