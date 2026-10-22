package com.homecash.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homecash.data.local.entity.ShiftCloseEntity
import com.homecash.data.repository.HomeCashRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class ShiftClosingViewModel(
    private val repository: HomeCashRepository
) : ViewModel() {

    private val _actualCash = MutableStateFlow("")
    val actualCash = _actualCash.asStateFlow()

    private val _notes = MutableStateFlow("")
    val notes = _notes.asStateFlow()

    private val _shiftClosedSuccess = MutableStateFlow(false)
    val shiftClosedSuccess = _shiftClosedSuccess.asStateFlow()

    val recentTransactions = repository.recentTransactions

    val expectedCash = combine(recentTransactions, repository.currentUser) { list, user ->
        // The 5,000 EGP opening float belongs only to the demo dataset; real accounts start at zero
        // until an opening cash balance is configured explicitly.
        val openingFloat = if (user?.isDemoMode == true) 5000.0 else 0.0
        val cashCollected = list.filter { it.direction == "OUT" }.sumOf { it.amount + it.cashierCommission }
        val cashPaidOut = list.filter { it.direction == "IN" }.sumOf { it.amount }
        (openingFloat + cashCollected - cashPaidOut).coerceAtLeast(0.0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun onActualCashChange(newVal: String) {
        _actualCash.value = newVal
    }

    fun onNotesChange(newVal: String) {
        _notes.value = newVal
    }

    fun submitShiftClose(onSuccess: () -> Unit) {
        val userId = repository.activeUserId()
        if (userId.isBlank()) return
        val actual = _actualCash.value.toDoubleOrNull() ?: 0.0
        val expected = expectedCash.value
        val diff = actual - expected

        val shift = ShiftCloseEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            deviceId = "dev-cashier-1",
            openedAt = System.currentTimeMillis() - 28800000L, // 8 hours ago
            closedAt = System.currentTimeMillis(),
            expectedCash = expected,
            actualCash = actual,
            difference = diff,
            totalInward = 0.0,
            totalOutward = 0.0,
            totalCommission = 0.0,
            notes = _notes.value
        )

        viewModelScope.launch {
            repository.closeShift(shift)
            _shiftClosedSuccess.value = true
            onSuccess()
        }
    }
}
