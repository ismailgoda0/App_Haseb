package com.homecash.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homecash.data.local.entity.TransactionEntity
import com.homecash.data.local.entity.WalletEntity
import com.homecash.data.repository.HomeCashRepository
import com.homecash.domain.model.CounterpartySummary
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TransactionsViewModel(
    private val repository: HomeCashRepository
) : ViewModel() {

    val wallets: StateFlow<List<WalletEntity>> = repository.wallets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedDirection = MutableStateFlow<String?>("ALL") // "ALL", "IN", "OUT"
    val selectedDirection = _selectedDirection.asStateFlow()

    private val _selectedWalletId = MutableStateFlow<String?>(null)
    val selectedWalletId = _selectedWalletId.asStateFlow()

    val transactions: StateFlow<List<TransactionEntity>> = combine(
        repository.allTransactions,
        _searchQuery,
        _selectedDirection,
        _selectedWalletId
    ) { list, query, direction, walletId ->
        list.filter { tx ->
            val matchesQuery = query.isBlank() ||
                    tx.counterpartyNumber.contains(query, ignoreCase = true) ||
                    tx.counterpartyName.contains(query, ignoreCase = true) ||
                    tx.operationCode.contains(query, ignoreCase = true) ||
                    tx.amount.toString().contains(query)

            val matchesDirection = direction == "ALL" || tx.direction == direction
            val matchesWallet = walletId == null || tx.walletId == walletId

            matchesQuery && matchesDirection && matchesWallet
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Counterparty summary modal
    private val _counterpartySummary = MutableStateFlow<CounterpartySummary?>(null)
    val counterpartySummary = _counterpartySummary.asStateFlow()

    fun onSearchChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onDirectionSelect(direction: String) {
        _selectedDirection.value = direction
    }

    fun onWalletSelect(walletId: String?) {
        _selectedWalletId.value = walletId
    }

    fun loadCounterparty(phoneOrName: String) {
        viewModelScope.launch {
            _counterpartySummary.value = repository.getCounterpartySummary(phoneOrName)
        }
    }

    fun clearCounterparty() {
        _counterpartySummary.value = null
    }

    fun addManualTransaction(
        walletId: String,
        direction: String,
        amount: Double,
        counterpartyNumber: String,
        counterpartyName: String,
        category: String,
        commission: Double,
        note: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.addManualTransaction(
                walletId = walletId,
                direction = direction,
                amount = amount,
                counterpartyNumber = counterpartyNumber,
                counterpartyName = counterpartyName,
                category = category,
                commission = commission,
                note = note
            )
            onSuccess()
        }
    }
}
