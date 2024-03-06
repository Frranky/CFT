package com.example.cft.data.mapper

import com.example.cft.domain.model.UserModel
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import java.lang.reflect.Type

fun JsonArray.apiToModel(): UserModel {
    val gSon = GsonBuilder().registerTypeAdapter(UserModel::class.java, ApiDeserializer()).create()
    return gSon.fromJson(this, UserModel::class.java)
}

fun String.savedToModel(): MutableList<UserModel> {
    val json = JsonParser.parseString(this).asJsonArray
    val users = mutableListOf<UserModel>()
    for (user in json) {
        users.add(
            UserModel(
                photo = user.asJsonObject.get("photo").asString,
                name = user.asJsonObject.get("name").asString,
                email = user.asJsonObject.get("email").asString,
                birthday = user.asJsonObject.get("birthday").asString,
                age = user.asJsonObject.get("age").asString.toInt(),
                address = user.asJsonObject.get("address").asString,
                phone = user.asJsonObject.get("phone").asString,
                city = user.asJsonObject.get("city").asString,
                state = user.asJsonObject.get("state").asString,
                country = user.asJsonObject.get("country").asString
            )
        )
    }
    return users
}

private class ApiDeserializer : JsonDeserializer<UserModel> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?,
    ): UserModel {
        val result = json!!.asJsonArray.get(0).asJsonObject
        val photo = result.get("picture").asJsonObject.get("large").asString
        val name = result.get("name").asJsonObject.get("first").asString + " " +
                result.get("name").asJsonObject.get("last").asString
        val email = result.get("email").asString
        val birthday = result.get("dob").asJsonObject.get("date").asString
        val age = result.get("dob").asJsonObject.get("age").asString.toInt()
        val address =
            result.get("location").asJsonObject.get("street").asJsonObject.get("number").asString + " " +
                    result.get("location").asJsonObject.get("street").asJsonObject.get("name").asString
        val phone = result.get("phone").asString
        val city = result.get("location").asJsonObject.get("city").asString
        val state = result.get("location").asJsonObject.get("state").asString
        val country = result.get("location").asJsonObject.get("country").asString

        return UserModel(
            photo,
            name,
            email,
            birthday.substringBeforeLast(
                delimiter = 'T',
                missingDelimiterValue = "T not found"
            ),
            age,
            address,
            phone,
            city,
            state,
            country
        )
    }
}