package com.homecash.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homecash.core.simulation.MockSmsSimulationService
import com.homecash.core.simulation.SimulationEvent
import com.homecash.data.local.AppDatabase
import com.homecash.data.local.entity.FlagEntity
import com.homecash.data.local.entity.TransactionEntity
import com.homecash.data.local.entity.UserEntity
import com.homecash.data.local.entity.WalletEntity
import com.homecash.data.repository.HomeCashRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: HomeCashRepository,
    private val db: AppDatabase
) : ViewModel() {

    val user: StateFlow<UserEntity?> = repository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val wallets: StateFlow<List<WalletEntity>> = repository.wallets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalBalance: StateFlow<Double> = wallets.map { list ->
        list.sumOf { it.currentBalance }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val recentTransactions: StateFlow<List<TransactionEntity>> = repository.recentTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val openFlags: StateFlow<List<FlagEntity>> = repository.openFlags
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isSimulationRunning = MockSmsSimulationService.isSimulationRunning

    private val _latestSimEvent = MutableStateFlow<SimulationEvent?>(null)
    val latestSimEvent = _latestSimEvent.asStateFlow()

    // Daily statistics
    val todayInflow = recentTransactions.map { list ->
        list.filter { it.direction == "IN" }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val todayOutflow = recentTransactions.map { list ->
        list.filter { it.direction == "OUT" }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val todayCommissions = recentTransactions.map { list ->
        list.sumOf { it.cashierCommission }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    init {
        // Collect live simulation events
        viewModelScope.launch {
            MockSmsSimulationService.simulationEvents.collect { event ->
                _latestSimEvent.value = event
            }
        }
    }

    fun triggerManualSimulation() {
        viewModelScope.launch {
            MockSmsSimulationService.triggerManualSimulation(db)
        }
    }

    fun resolveFlag(flagId: String, action: String) {
        viewModelScope.launch {
            repository.resolveFlag(flagId, action)
        }
    }
}
