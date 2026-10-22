package com.homecash.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homecash.core.simulation.MockSmsSimulationService
import com.homecash.data.local.AppDatabase
import com.homecash.data.local.entity.DeviceEntity
import com.homecash.data.local.entity.UserEntity
import com.homecash.data.repository.HomeCashRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: HomeCashRepository,
    private val db: AppDatabase
) : ViewModel() {

    val user: StateFlow<UserEntity?> = repository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val devices: StateFlow<List<DeviceEntity>> = repository.devices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isSimulationRunning = MockSmsSimulationService.isSimulationRunning

    fun toggleAppMode() {
        val current = user.value ?: return
        val newMode = if (current.mode == "business") "personal" else "business"
        viewModelScope.launch {
            repository.setMode(newMode)
        }
    }

    fun toggleSimulation(enable: Boolean) {
        if (user.value?.isDemoMode != true) return
        if (enable) {
            MockSmsSimulationService.startSimulation(db, viewModelScope)
        } else {
            MockSmsSimulationService.stopSimulation()
        }
    }

    fun triggerMockSmsNow() {
        if (user.value?.isDemoMode != true) return
        viewModelScope.launch {
            MockSmsSimulationService.triggerManualSimulation(db)
        }
    }
}
