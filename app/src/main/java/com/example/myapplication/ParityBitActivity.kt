package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme

class ParityBitActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color=Color.White
                ) {
                    val scaffoldState = rememberScaffoldState()
                    Scaffold(
                        scaffoldState = scaffoldState,
                        topBar = {
                            AppBar(
                                title = "Cheacksum generation ",
                                onNavigationIconClick = {
                                    onBackPressed() // Handle back navigation
                                }
                            )
                        },
                        floatingActionButton = {
                            HelpDialogFAB(
                                explanationText = """
        Parity Bit Overview:
        Parity bits are added to binary data to make the number of 1s either even or odd, depending on the parity scheme. This helps detect errors during data transmission.

        How to Use:
        1. Enter your binary data in the input box.
        2. Select either 'Even' or 'Odd' parity from the options.
        3. Choose the action: 'Sender' to calculate and add a parity bit, or 'Receiver' to check the parity.
        4. Click 'Calculate' to either generate the parity bit or verify if the received data has errors.

        Use Case:
        Parity bits are commonly used in digital communication systems to detect simple errors in data transmission, offering a basic level of error detection.
    """.trimIndent(),

                            )
                        },
                    ) { PaddingValues ->
                        ParityBitApp(PaddingValues)
                    }
                }
            }
            }
        }
    }


@Composable
fun ParityBitApp(paddingValues: PaddingValues) {
    // State variables to store inputs and results
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(paddingValues) // Apply padding values from Scaffold to avoid overlap
    ) {

        var binaryInput by remember { mutableStateOf("") }
        var parityType by remember { mutableStateOf("Even") }
        var action by remember { mutableStateOf("Sender") }
        var outputBinary by remember { mutableStateOf("") }
        var result by remember { mutableStateOf("") }
        var steps by remember { mutableStateOf(listOf<String>()) } // For storing step-by-step info

        // Parity bit calculation logic with steps
        fun calculateParity(binary: String, isEven: Boolean): String {
            val countOnes = binary.count { it == '1' }
            val parityBit = if (isEven) {
                if (countOnes % 2 == 0) "0" else "1"
            } else {
                if (countOnes % 2 == 0) "1" else "0"
            }
            steps = listOf(
                "Step 1: Count the number of 1's in the binary string: $countOnes",
                if (isEven) "Step 2: Even parity selected" else "Step 2: Odd parity selected",
                "Step 3: Parity bit calculated as: $parityBit"
            )
            return binary + parityBit
        }

        fun checkParity(binary: String, isEven: Boolean): Boolean {
            val countOnes = binary.count { it == '1' }
            steps = listOf(
                "Step 1: Count the number of 1's in the binary string: $countOnes",
                if (isEven) "Step 2: Even parity selected" else "Step 2: Odd parity selected",
                "Step 3: Checking if the parity bit is correct"
            )
            return if (isEven) countOnes % 2 == 0 else countOnes % 2 != 0
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color.White)
        ) {
            // Binary Input
            Text(text = "Binary Input", color = Color.Black, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = binaryInput,
                onValueChange = { binaryInput = it },
                label = { Text("Enter Binary String", color = Color.Black) },
                modifier = Modifier.fillMaxWidth(),

                textStyle = LocalTextStyle.current.copy(color = Color.Black),


                placeholder = { Text("Enter binary (e.g.,1110101 )", color = Color.Black) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),

                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black
                )
            )

            // Parity Type Selection
            Text(text = "Parity Type", color = Color.Black, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                RadioButton(
                    selected = parityType == "Even",
                    onClick = { parityType = "Even" }
                )
                Text(text = "Even", color = Color.Black)
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = parityType == "Odd",
                    onClick = { parityType = "Odd" }
                )
                Text(text = "Odd", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Selection (Sender or Receiver)
            Text(text = "Action", color = Color.Black, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                RadioButton(
                    selected = action == "Sender",
                    onClick = { action = "Sender" }
                )
                Text(text = "Sender", color = Color.Black)
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = action == "Receiver",
                    onClick = { action = "Receiver" }
                )
                Text(text = "Receiver", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Calculate Button
            Button(
                onClick = {
                    steps = emptyList() // Reset steps
                    if (action == "Sender") {
                        outputBinary = calculateParity(binaryInput, parityType == "Even")
                        result = "Parity Bit Added: $outputBinary"
                    } else if (action == "Receiver") {
                        val isValid = checkParity(binaryInput, parityType == "Even")
                        result = if (isValid) "Parity Check Passed" else "Parity Error Detected"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text("Calculate")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Output Section
            Text(text = "Result: $result", color = Color.Black, fontSize = 16.sp)

            // Show steps
            if (steps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Steps:", color = Color.Black, fontSize = 18.sp)
                Column(modifier = Modifier.fillMaxHeight().padding(8.dp)) {
                    steps.forEach { step ->
                        Text(text = step, color = Color.Gray, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}
