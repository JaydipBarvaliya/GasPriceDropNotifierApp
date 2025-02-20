
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gpn.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.await

class GasPriceViewModel : ViewModel() {

    private val _stationsState = MutableStateFlow<List<GasStation>>(emptyList())
    val stationsState: StateFlow<List<GasStation>> = _stationsState.asStateFlow()

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()

    // Fuel type mapping
    private val fuelTypeMap = mapOf(
        "Regular" to 1,
        "Midgrade" to 2,
        "Premium" to 3,
        "Diesel" to 4,
        "E85" to 5,
        "UNL88" to 12
    )

    fun getFuelTypeId(fuelTypeName: String): Int {
        return fuelTypeMap[fuelTypeName] ?: 1
    }

    fun fetchStations(search: String, fuel: Int, maxAge: Int, brandId: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.gasPriceApi
                    .findByCityOrZipcode(search, fuel, maxAge, brandId)
                    .await()

                val stations = response.data
                    ?.locationBySearchTerm
                    ?.stations
                    ?.results ?: emptyList()

                withContext(Dispatchers.Main) {
                    _stationsState.value = stations
                    _errorState.value = null
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _errorState.value = "Failed to fetch stations: ${e.localizedMessage}"
                }
            }
        }
    }



    private val _brands = MutableStateFlow<List<Brand>>(emptyList())
    val brands: StateFlow<List<Brand>> = _brands.asStateFlow()

    private val _selectedBrand = MutableStateFlow<Brand?>(null)
    val selectedBrand: StateFlow<Brand?> = _selectedBrand.asStateFlow()

    init {
        fetchBrands()
    }

    private fun fetchBrands() {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.gasPriceApi.getBrands()
                }

                val brandList = response.data.brands
                _brands.value = brandList  // Assuming _brands is a MutableStateFlow or LiveData
            } catch (e: Exception) {
                Log.e("GasPriceViewModel", "Error fetching brands", e)
            }
        }
    }



    fun setSelectedBrand(brand: Brand) {
        _selectedBrand.value = brand
    }
}

data class BrandResponse(val data: BrandData)
data class BrandData(val brands: List<Brand>)
data class Brand(val id: String, val name: String)