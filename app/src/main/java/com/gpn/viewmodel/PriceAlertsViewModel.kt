package com.gpn.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gpn.network.Alert
import com.gpn.network.GasPriceApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PriceAlertsViewModel @Inject constructor(
    private val gasPriceApi: GasPriceApi
) : ViewModel() {

    private val _alerts = MutableStateFlow<List<Alert>>(emptyList())
    val alerts: StateFlow<List<Alert>> = _alerts

    private val _updateStatus = MutableStateFlow<Boolean?>(null)
    val updateStatus: StateFlow<Boolean?> = _updateStatus

    private val _deleteStatus = MutableStateFlow<Boolean?>(null)
    val deleteStatus: StateFlow<Boolean?> = _deleteStatus

    private val _deleteAllStatus = MutableStateFlow<Boolean?>(null)
    val deleteAllStatus: StateFlow<Boolean?> = _deleteAllStatus

    fun fetchAlerts() {
        viewModelScope.launch {
            try {
                _alerts.value = gasPriceApi.getAlerts()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateAlert(alert: Alert) {
        viewModelScope.launch {
            try {
                val response = gasPriceApi.updateAlert(alert)
                _updateStatus.value = response.isSuccessful
            } catch (e: Exception) {
                _updateStatus.value = false
            }
        }
    }

    fun deleteAlert(id: Long) {
        viewModelScope.launch {
            try {
                val response = gasPriceApi.deleteAlert(id)
                _deleteStatus.value = response.isSuccessful
            } catch (e: Exception) {
                _deleteStatus.value = false
            }
        }
    }

    fun deleteAllAlerts() {
        viewModelScope.launch {
            try {
                val response = gasPriceApi.deleteAllAlerts()
                _deleteAllStatus.value = response.isSuccessful
            } catch (e: Exception) {
                _deleteAllStatus.value = false
            }
        }
    }
}
