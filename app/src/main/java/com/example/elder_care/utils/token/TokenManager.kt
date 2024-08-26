package com.example.elder_care.utils.token

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

//@Singleton
//class TokenManager @Inject constructor(
//    @ApplicationContext private val context: Context
//) {
//    companion object {
//        private val FCM_TOKEN = stringPreferencesKey("fcm_token")
//        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
//        private val COOKIE = stringPreferencesKey("cookie")
//    }
//
//    private val dataStore: DataStore<Preferences> = context.datastore
//
//    fun saveToken(token: String) = runBlocking {
//        dataStore.edit { prefs ->
//            prefs[ACCESS_TOKEN_KEY] = token
//        }
//    }
//
//    fun getAccessToken(): Flow<String?> {
//        return dataStore.data.map { prefs ->
//            prefs[ACCESS_TOKEN_KEY]
//        }
//    }
//
//    suspend fun deleteAccessToken() {
//        dataStore.edit { prefs ->
//            prefs.remove(ACCESS_TOKEN_KEY)
//        }
//    }
//
//    // FCM 토큰 저장
//    suspend fun saveFcmToken(fcmToken: String) {
//        dataStore.edit { prefs ->
//            prefs[FCM_TOKEN] = fcmToken
//        }
//    }
//
//    fun getFcmToken() : Flow<String?> {
//        return dataStore.data.map { prefs ->
//            prefs[FCM_TOKEN]
//        }
//    }
//}