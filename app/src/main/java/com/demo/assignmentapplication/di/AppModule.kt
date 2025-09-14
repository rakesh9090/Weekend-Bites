package com.demo.assignmentapplication.di

import android.content.Context
import androidx.room.Room
import com.demo.assignmentapplication.data.local.AppDatabase
import com.demo.assignmentapplication.data.local.SampleDao
import com.demo.assignmentapplication.data.remote.ApiServices
import com.demo.assignmentapplication.data.repository.SampleRepository
import com.demo.assignmentapplication.data.repository.SampleRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    const val BASE_URL = "https://35dee773a9ec441e9f38d5fc249406ce.api.mockbin.io/"
    const val DATABASE_NAME = "AssignmentDatabase"

    @Provides
    @Singleton
    fun provideRetroFit(): Retrofit =
        Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiServices = retrofit.create(ApiServices::class.java)

    @Singleton
    @Provides
    fun getLocalDataBase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "base_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideSampleDao(db: AppDatabase): SampleDao = db.getDao()

    @Provides
    @Singleton
    fun provideSampleRepository(
        api: ApiServices,
        dao: SampleDao
    ): SampleRepository = SampleRepositoryImpl(api, dao)

}