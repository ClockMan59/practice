package ci.nsu.moble.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CounterScreen() // Вот тут мы вызываем наш экран!
        }
    }
}

// Сюда же для удобства закинем UI-часть
@Composable
fun CounterScreen(counterViewModel: CounterViewModel = viewModel()) {
    val state by counterViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().systemBarsPadding().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "${state.count}", style = MaterialTheme.typography.displayLarge)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { counterViewModel.increment() }) { Text("+") }
            Button(onClick = { counterViewModel.decrement() }) { Text("-") }
            Button(onClick = { counterViewModel.reset() }) { Text("Сброс") }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "История:", style = MaterialTheme.typography.headlineSmall)

        LazyColumn {
            items(state.history) { log ->
                Text(text = log, modifier = Modifier.padding(4.dp))
            }
        }
    }
}