package com.homecash.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homecash.data.local.entity.TransactionEntity
import com.homecash.data.local.entity.WalletEntity
import com.homecash.data.repository.HomeCashRepository
import kotlinx.coroutines.flow.*

data class WalletReportItem(
    val walletName: String,
    val balance: Double,
    val totalIn: Double,
    val totalOut: Double,
    val commission: Double,
    val colorHex: String
)

class ReportsViewModel(
    private val repository: HomeCashRepository
) : ViewModel() {

    private val _selectedPeriod = MutableStateFlow("MONTH") // "TODAY", "WEEK", "MONTH", "ALL"
    val selectedPeriod = _selectedPeriod.asStateFlow()

    val wallets: StateFlow<List<WalletEntity>> = repository.wallets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredTransactions: StateFlow<List<TransactionEntity>> = combine(
        allTransactions,
        _selectedPeriod
    ) { list, period ->
        val now = System.currentTimeMillis()
        val filterTime = when (period) {
            "TODAY" -> now - 86400000L
            "WEEK" -> now - (7 * 86400000L)
            "MONTH" -> now - (30 * 86400000L)
            else -> 0L
        }
        list.filter { it.occurredAt >= filterTime }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalIn = filteredTransactions.map { list ->
        list.filter { it.direction == "IN" }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalOut = filteredTransactions.map { list ->
        list.filter { it.direction == "OUT" }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalCommissions = filteredTransactions.map { list ->
        list.sumOf { it.cashierCommission }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val walletReports: StateFlow<List<WalletReportItem>> = combine(
        wallets,
        filteredTransactions
    ) { wList, txList ->
        wList.map { w ->
            val wTxs = txList.filter { it.walletId == w.id }
            WalletReportItem(
                walletName = w.providerName,
                balance = w.currentBalance,
                totalIn = wTxs.filter { it.direction == "IN" }.sumOf { it.amount },
                totalOut = wTxs.filter { it.direction == "OUT" }.sumOf { it.amount },
                commission = wTxs.sumOf { it.cashierCommission },
                colorHex = w.colorHex
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectPeriod(period: String) {
        _selectedPeriod.value = period
    }
}
