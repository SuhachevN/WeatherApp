package ru.suhachev.weatherapp.presentation.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import ru.suhachev.weatherapp.data.network.GeocodingApiService
import ru.suhachev.weatherapp.domain.model.CityAutocompleteModel
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(FlowPreview::class)
@Singleton
class CityAutocompleteService @Inject constructor(
    private val geocodingApi: GeocodingApiService
) {
    companion object {
        private const val DEBOUNCE_DELAY = 300L
        private const val MIN_QUERY_LENGTH = 2
    }

    private val queryFlow = MutableSharedFlow<String>()
    private val cache = mutableMapOf<String, List<CityAutocompleteModel>>()

    val suggestionsFlow: Flow<List<CityAutocompleteModel>> = queryFlow
        .debounce(DEBOUNCE_DELAY)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            flow {
                if (query.length < MIN_QUERY_LENGTH) {
                    emit(emptyList())
                    return@flow
                }

                cache[query.lowercase()]?.let {
                    emit(it)
                    return@flow
                }

                try {
                    val response = geocodingApi.searchCity(
                        name = query,
                        count = 10,
                        language = "ru"
                    )
                    
                    val suggestions = response.results?.map { result ->
                        CityAutocompleteModel(
                            id = result.id ?: 0,
                            name = result.name ?: "",
                            country = result.country ?: "",
                            latitude = result.latitude ?: 0.0,
                            longitude = result.longitude ?: 0.0,
                            population = null,
                            elevation = null
                        )
                    } ?: emptyList()

                    cache[query.lowercase()] = suggestions
                    emit(suggestions)
                } catch (e: Exception) {
                    emit(emptyList())
                }
            }
        }
        .flowOn(Dispatchers.IO)

    suspend fun search(query: String) {
        queryFlow.emit(query)
    }
}