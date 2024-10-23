package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

@Composable
fun HelpDialogFAB(explanationText: String) {
    var openDialog by remember { mutableStateOf(false) }

    // Floating Action Button
    FloatingActionButton(
        onClick = { openDialog = true },
        modifier = Modifier.padding(16.dp),
        content = {
            Icon(imageVector = Icons.Default.Info, contentDescription = "Help")
        }
    )

    // Dialog that will appear when FAB is clicked
    if (openDialog) {
        Dialog(onDismissRequest = { openDialog = false }) {
            Surface(
                modifier = Modifier.size(400.dp,600.dp)
                 ,
                shape = MaterialTheme.shapes.large,
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    // Cross button to dismiss the dialog
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
                        IconButton(onClick = { openDialog = false }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    // Scrollable content area for explanation
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = explanationText,
                            style = MaterialTheme.typography.body1,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}
