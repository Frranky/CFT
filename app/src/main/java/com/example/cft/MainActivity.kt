package com.example.cft

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.cft.data.repository.UserRepository
import com.example.cft.domain.model.UserModel
import com.example.cft.ui.theme.CFTTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var response = mutableListOf<UserModel>()
    private var userId = 0
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this.baseContext
        val repository = UserRepository(context)
        GlobalScope.launch {
            try {
                response = repository.loadSaved()
            } catch (e: Exception) {
                response.clear()
                Log.v("API ERROR", e.message!!)
            }

            launch(Dispatchers.Main) {
                setContent {
                    var users by remember { mutableStateOf(response) }
                    var flag by remember { mutableStateOf(true) }
                    CFTTheme {
                        if (users.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                            ) {
                                Scaffold(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .alpha(if (flag) 1f else 0f),
                                    floatingActionButton = {
                                        FloatingActionButton(onClick = {
                                            GlobalScope.launch {
                                                try {
                                                    launch(Dispatchers.Main) {
                                                        Toast.makeText(context, "Refreshing...", Toast.LENGTH_LONG).show()
                                                    }
                                                    users = repository.fetchUsers()
                                                } catch (e: Exception) {
                                                    users.clear()
                                                    Log.v("API ERROR", e.message!!)
                                                }
                                            }
                                        }) {
                                            Icon(Icons.Default.Refresh, contentDescription = "Add")
                                        }
                                    }
                                ) { innerPadding ->
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.White)
                                            .padding(innerPadding)
                                    ) {
                                        items(users.size) { index ->
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        flag = false
                                                        userId = index
                                                    }
                                            ) {
                                                User(
                                                    users[index].name,
                                                    users[index].address,
                                                    users[index].phone,
                                                    users[index].photo,
                                                    index
                                                )
                                            }
                                        }
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .alpha(if (!flag) 1f else 0f)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Column {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(users[userId].photo)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = "User image",
                                            modifier = Modifier
                                                .width(Dp(100f))
                                                .height(Dp(100f))
                                                .padding(Dp(5f)),
                                        )
                                        Text(text = "Name:  ${users[userId].name}")
                                        Text(text = "email:  ${users[userId].email}")
                                        Text(
                                            text = "Phone:  ${users[userId].phone}",
                                            modifier = Modifier.clickable {
                                                val callIntent = Intent(Intent.ACTION_CALL)
                                                callIntent.setData(android.net.Uri.parse("tel:${users[userId].phone}"))
                                                if (ContextCompat.checkSelfPermission(context,
                                                    Manifest.permission.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION)
                                            }
                                        )
                                        Text(text = "Birthday:  ${users[userId].birthday}")
                                        Text(text = "Age:  ${users[userId].age}")
                                        Text(text = "Country:  ${users[userId].country}")
                                        Text(text = "State:  ${users[userId].state}")
                                        Text(text = "City:  ${users[userId].city}")
                                        Text(text = "Address:  ${users[userId].address}")
                                        OutlinedButton(
                                            onClick = { flag = true },
                                        ) {
                                            Text(text = "Back")
                                        }
                                    }
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) { Text(text = "Some errors occurred!") }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun User(fio: String, address: String, phone: String, photo: String, id: Int) {
        userId = id
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dp(5f))
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photo)
                    .crossfade(true)
                    .build(),
                contentDescription = "User image",
                modifier = Modifier
                    .width(Dp(70f))
                    .height(Dp(70f))
                    .padding(Dp(5f)),
            )
            Column(
                modifier = Modifier.padding(horizontal = Dp(5f))
            ) {
                Text(text = fio)
                Text(text = address)
                Text(text = phone)
            }
        }
    }
}
