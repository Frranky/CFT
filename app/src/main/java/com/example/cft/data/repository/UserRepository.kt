package com.example.cft.data.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.cft.data.api.RandomApi
import com.example.cft.data.mapper.apiToModel
import com.example.cft.data.mapper.savedToModel
import com.example.cft.domain.model.UserModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class UserRepository(private val context: Context) {

    fun fetchUsers(): MutableList<UserModel> {
        val users = mutableListOf<UserModel>()
        try {
            for (i in 0..10) {
                users.add(RandomApi.get().apiToModel())
                Log.v("TAG $i", users[i].toString())
            }
            saveData(users)
        } catch (e: Exception) {
            users.clear()
            GlobalScope.launch(Dispatchers.Main) {
                Toast.makeText(context, "Failed to fetch data.", Toast.LENGTH_LONG).show()
            }
            Log.v("API ERROR", e.message!!)
        } finally {
            return users
        }
    }

    fun loadSaved(): MutableList<UserModel> {
        try {
            val path = context.filesDir
            val letDirectory = File(path, "json")
            val file = File(letDirectory, "data.json")
            if (file.exists()) {
                return (FileInputStream(file).bufferedReader().use { it.readText() }).savedToModel()
            }
            return fetchUsers()
        } catch (e: IOException) {
            Log.v("CRITICAL ERROR", e.message!!)
            GlobalScope.launch(Dispatchers.Main) {
                Toast.makeText(context, "CRITICAL ERROR", Toast.LENGTH_LONG).show()
            }
            return mutableListOf<UserModel>()
        }
    }

    private fun saveData(data: MutableList<UserModel>) {
        try {
            val json = Gson().toJson(data)
            val path = context.filesDir
            val letDirectory = File(path, "json")
            letDirectory.mkdirs()
            val file = File(letDirectory, "data.json")
            file.writeText(json)
            Log.v("SAVED", json)
        } catch (e: IOException) {
            Log.v("SAVING ERROR", e.message!!)
            GlobalScope.launch(Dispatchers.Main) {
                Toast.makeText(context, "SAVE ERROR", Toast.LENGTH_LONG).show()
            }
        }
    }
}