package com.gpn.viewmodel

import android.util.Log
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
                val alertList = gasPriceApi.getAlerts()
                _alerts.value = alertList
            } catch (e: Exception) {
                Log.e("FetchAlerts", "Error fetching alerts", e)
            }
        }
    }

    fun updateAlert(updatedAlert: Alert) {
        viewModelScope.launch {
            try {
                val response = gasPriceApi.updateAlert(updatedAlert) // API Call
                if (response.isSuccessful) { // Ensure success response
                    response.body()?.let { updatedAlerts ->
                        _alerts.value = updatedAlerts // Update UI with new alerts list
                    }
                } else {
                    // Handle API failure (e.g., log or show error message)
                    Log.e("UpdateAlert", "Failed to update alert: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("UpdateAlert", "Error updating alert", e)
            }
        }
    }


    fun deleteAlert(id: Long) {
        viewModelScope.launch {
            try {
                val response = gasPriceApi.deleteAlert(id)
                if (response.isSuccessful) {
                    response.body()?.let { updatedAlerts ->
                        _alerts.value = updatedAlerts // Update UI with new list
                        _deleteStatus.value = true
                    } ?: run {
                        Log.e("DeleteAlert", "Empty response body")
                        _deleteStatus.value = false
                    }
                } else {
                    Log.e("DeleteAlert", "Failed to delete alert: ${response.errorBody()?.string()}")
                    _deleteStatus.value = false
                }
            } catch (e: Exception) {
                Log.e("DeleteAlert", "Error deleting alert", e)
                _deleteStatus.value = false
            }
        }
    }


    fun deleteAllAlerts() {
        viewModelScope.launch {
            try {
                val response = gasPriceApi.deleteAllAlerts()

                if (response.isSuccessful) {
                    _deleteAllStatus.value = true // Success
                } else {
                    Log.e("DeleteAllAlerts", "Failed to delete alerts: ${response.errorBody()?.string()}")
                    _deleteAllStatus.value = false // Failure
                }
            } catch (e: Exception) {
                Log.e("DeleteAllAlerts", "Error deleting all alerts", e)
                _deleteAllStatus.value = false
            }
        }
    }


}
