package com.demo.assignmentapplication.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.demo.assignmentapplication.ui.theme.AssignmentApplicationTheme
import com.demo.assignmentapplication.ui.viewmodel.SampleViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AssignmentApplicationTheme {
                val viewModel: SampleViewModel = hiltViewModel()
                val state by viewModel.state.collectAsState()

                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    when {
                        state.isLoading -> Text("Loading...", modifier = Modifier.padding(padding))
                        state.error != null -> Text("Error: ${state.error}", modifier = Modifier.padding(padding))
                        else -> LazyColumn(modifier = Modifier.padding(padding)) {
                            items(state.data) { item ->
                                //Text("${item.name} - ${item.age}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AssignmentApplicationTheme {
        Greeting("Android")
    }
}