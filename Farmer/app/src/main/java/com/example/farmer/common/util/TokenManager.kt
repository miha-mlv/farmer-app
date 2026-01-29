package com.example.farmer.common.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit

class TokenManager(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "token_seif",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveToken(token: String) {
        sharedPreferences.edit { putString("jwt_token", token) }
    }

    fun getToken(): String? {
        return sharedPreferences.getString("jwt_token", null)
    }

    fun updateToken(token: String) {
        sharedPreferences.edit { putString("jwt_token", token)}
    }

    fun deleteToken() {
        sharedPreferences.edit { remove("jwt_token") }
    }

    fun saveRole(role: String){
        sharedPreferences.edit { putString("role", role) }
    }

    fun getRole(): String? {
        return sharedPreferences.getString("role", null)
    }

    fun isLoggedIn(): Boolean = !getToken().isNullOrEmpty()


    fun logout(){
        sharedPreferences.edit{ clear() }
    }

}