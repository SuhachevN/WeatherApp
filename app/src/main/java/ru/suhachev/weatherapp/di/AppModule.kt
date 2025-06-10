package ru.suhachev.weatherapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.suhachev.weatherapp.data.network.WeatherApiService
import ru.suhachev.weatherapp.data.repository.WeatherRepositoryImpl
import ru.suhachev.weatherapp.domain.repository.WeatherRepository
import ru.suhachev.weatherapp.domain.usecase.GetCurrentWeatherUseCase
import ru.suhachev.weatherapp.domain.usecase.GetHourlyWeatherUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val BASE_URL = "https://api.weatherapi.com/v1/"
    private const val API_KEY = "7fab0999bb4146c7956124829250706"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideWeatherApi(retrofit: Retrofit): WeatherApiService =
        retrofit.create(WeatherApiService::class.java)

    @Provides
    @Singleton
    fun provideWeatherRepository(api: WeatherApiService): WeatherRepository =
        WeatherRepositoryImpl(api, API_KEY)

    @Provides
    @Singleton
    fun provideGetCurrentWeatherUseCase(repository: WeatherRepository): GetCurrentWeatherUseCase =
        GetCurrentWeatherUseCase(repository)

    @Provides
    @Singleton
    fun provideGetHourlyWeatherUseCase(repository: WeatherRepository): GetHourlyWeatherUseCase =
        GetHourlyWeatherUseCase(repository)
} 