package com.gabpawang.app.feature.record

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gabpawang.app.GabpaApplication
import com.gabpawang.app.R
import com.gabpawang.app.data.db.WorkoutSessionEntity
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*

data class RecordUiState(
    val totalReps: Int = 0,
    val streak: Int = 0,
    val oneRepMax: Int = 0,
    val dailyAvg: Int = 0,
    val sessionsByDate: Map<String, Int> = emptyMap(),
    val sessionDetailsByDate: Map<String, List<WorkoutSessionEntity>> = emptyMap(),
    val weeklyReps: List<Int> = List(7) { 0 },
    val weeklyLabels: List<String> = List(7) { "" },
    val monthlyReps: List<Int> = List(5) { 0 },
    val monthlyLabels: List<String> = List(5) { "" },
    val maxDailyRepsDay: String = "-",
    val maxDailyReps: Int = 0,
    val maxDurationSec: Int = 0,
    val maxDurationDay: String = "-",
    val maxSets: Int = 0,
    val maxSetsDay: String = "-"
)

class RecordViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = (application as GabpaApplication).repository

    val uiState: StateFlow<RecordUiState> = combine(
        repo.allSessions,
        repo.totalReps,
        repo.streakFlow()
    ) { sessions, total, streak ->
        buildUiState(sessions, total, streak)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RecordUiState())

    private fun buildUiState(
        sessions: List<WorkoutSessionEntity>,
        total: Int,
        streak: Int
    ): RecordUiState {
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val monthFmt = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val monthLabelFmt = SimpleDateFormat(
            getApplication<Application>().getString(R.string.calendar_md_format).let { pattern ->
                // Use "MMM" for English locales, "M월" for Korean — fall back to short form.
                if (pattern.contains("월")) "M월" else "MMM"
            },
            Locale.getDefault()
        )
        val dayOfWeekFmt = SimpleDateFormat("E", Locale.getDefault())
        val dashLabel = getApplication<Application>().getString(R.string.record_dash)

        // Sessions grouped by date
        val sessionsByDateRaw = sessions.groupBy { fmt.format(Date(it.dateMillis)) }
        val byDate = sessionsByDateRaw.mapValues { (_, list) -> list.sumOf { it.totalReps } }

        // 1RM estimate: max (totalReps / sets) across all sessions
        val oneRepMax = sessions.maxOfOrNull { s ->
            if (s.sets > 0) s.totalReps / s.sets else s.totalReps
        } ?: 0

        // Daily average across distinct workout days
        val distinctDays = byDate.keys.size
        val dailyAvg = if (distinctDays > 0) total / distinctDays else 0

        // Last 7 days (oldest to newest, today is index 6)
        val cal = Calendar.getInstance()
        val weeklyReps = mutableListOf<Int>()
        val weeklyLabels = mutableListOf<String>()
        val tempCal = cal.clone() as Calendar
        tempCal.add(Calendar.DAY_OF_YEAR, -6)
        for (i in 0..6) {
            val dateStr = fmt.format(tempCal.time)
            val label = dayOfWeekFmt.format(tempCal.time)
            weeklyReps.add(byDate[dateStr] ?: 0)
            weeklyLabels.add(label)
            tempCal.add(Calendar.DAY_OF_YEAR, 1)
        }

        // Last 5 months (oldest to newest)
        val monthlyReps = mutableListOf<Int>()
        val monthlyLabels = mutableListOf<String>()
        val monthCal = cal.clone() as Calendar
        monthCal.add(Calendar.MONTH, -4)
        for (i in 0..4) {
            val monthKey = monthFmt.format(monthCal.time)
            val label = monthLabelFmt.format(monthCal.time)
            val monthTotal = sessions
                .filter { monthFmt.format(Date(it.dateMillis)) == monthKey }
                .sumOf { it.totalReps }
            monthlyReps.add(monthTotal)
            monthlyLabels.add(label)
            monthCal.add(Calendar.MONTH, 1)
        }

        // Top 3 records
        val bestDay = byDate.maxByOrNull { it.value }
        val bestDuration = sessions.maxByOrNull { it.durationSec }
        val bestSets = sessions.maxByOrNull { it.sets }

        return RecordUiState(
            totalReps = total,
            streak = streak,
            oneRepMax = oneRepMax,
            dailyAvg = dailyAvg,
            sessionsByDate = byDate,
            sessionDetailsByDate = sessionsByDateRaw,
            weeklyReps = weeklyReps,
            weeklyLabels = weeklyLabels,
            monthlyReps = monthlyReps,
            monthlyLabels = monthlyLabels,
            maxDailyRepsDay = bestDay?.key ?: dashLabel,
            maxDailyReps = bestDay?.value ?: 0,
            maxDurationSec = bestDuration?.durationSec ?: 0,
            maxDurationDay = bestDuration?.let { fmt.format(Date(it.dateMillis)) } ?: dashLabel,
            maxSets = bestSets?.sets ?: 0,
            maxSetsDay = bestSets?.let { fmt.format(Date(it.dateMillis)) } ?: dashLabel
        )
    }
}
