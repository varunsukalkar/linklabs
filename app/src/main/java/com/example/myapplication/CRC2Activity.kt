package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme

class CRC2Activity : ComponentActivity() {

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
                                title = "CRC",
                                onNavigationIconClick = {
                                    onBackPressed() // Handle back navigation
                                }
                            )
                        },
                        floatingActionButton = {
                            HelpDialogFAB(
                                explanationText = """
        CRC Overview:
        The Cyclic Redundancy Check (CRC) is an error-detection technique used to identify accidental changes to raw data. It calculates a short, fixed-length binary sequence, called a checksum, for each block of data.

        How to Use:
        1. Enter your data and divisor in the respective input fields.
        2. Click 'Calculate' to compute the CRC. The app will display the CRC and all intermediate steps.
        3. Use the CRC result to detect any data transmission errors.

        Use Case:
        CRC is widely used in networks and storage devices to detect errors in data transmission and storage, ensuring that data integrity is maintained.
    """.trimIndent(),

                            )
                        },
                    ) { PaddingValues ->
                        CRCApp(PaddingValues)
                    }
                }
            }
        }
    }
}

@Composable
fun CRCApp(paddingValues: PaddingValues) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(paddingValues) // Apply padding values from Scaffold to avoid overlap
    ) {
        // State variables to hold user input and results
        var dataString by rememberSaveable { mutableStateOf("") }
        var divisor by rememberSaveable { mutableStateOf("") }
        var transmittedData by rememberSaveable { mutableStateOf("") }
        var crcValue by rememberSaveable { mutableStateOf("") }
        var validationMessage by rememberSaveable { mutableStateOf("") }
        var showAlert by rememberSaveable { mutableStateOf(false) }

        // CRC calculation logic translated from C++ to Kotlin
        fun xor1(a: String, b: String): String {
            var result = ""
            for (i in 1 until b.length) {
                result += if (a[i] == b[i]) '0' else '1'
            }
            return result
        }

        fun mod2div(dividend: String, divisor: String): String {
            var pick = divisor.length
            var tmp = dividend.substring(0, pick)

            while (pick < dividend.length) {
                tmp = if (tmp[0] == '1')
                    xor1(divisor, tmp) + dividend[pick]
                else
                    xor1("0".repeat(divisor.length), tmp) + dividend[pick]

                pick += 1
            }

            if (tmp[0] == '1')
                tmp = xor1(divisor, tmp)
            else
                tmp = xor1("0".repeat(divisor.length), tmp)

            return tmp
        }

        fun encodeData(data: String, key: String): Pair<String, String> {
            val l_key = key.length
            val appendedData = data + "0".repeat(l_key - 1)
            val remainder = mod2div(appendedData, key)
            val codeword = data + remainder
            return codeword to remainder
        }

        fun receiver(data: String, key: String): String {
            var currxor = mod2div(data.substring(0, key.length), key)
            var curr = key.length

            while (curr != data.length) {
                if (currxor.length != key.length) {
                    currxor += data[curr++]
                } else {
                    currxor = mod2div(currxor, key)
                }
            }

            if (currxor.length == key.length) {
                currxor = mod2div(currxor, key)
            }

            return if (currxor.contains('1')) {
                "There is some error in data"
            } else {
                "Correct message received"
            }
        }

        // UI layout with a white background
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // CardView: Data Input and CRC Calculation
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .border(BorderStroke(1.dp, Color.Black)),
                backgroundColor = Color.White,
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()) // Make the UI scrollable
                ) {
                    // Header Text
                    Text(
                        text = "Data Input",
                        color = Color.Black,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Data String Input
                    OutlinedTextField(
                        value = dataString,
                        onValueChange = { dataString = it },
                        label = { Text("Enter Data String", color = Color.Black) },


                        placeholder = {
                            Text(
                                "Enter binary (e.g.,100100000 )",
                                color = Color.Black
                            )
                        },

                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        textStyle = LocalTextStyle.current.copy(color = Color.Black),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.Black,
                            cursorColor = Color.Black,
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Divisor Input
                    OutlinedTextField(
                        value = divisor,
                        onValueChange = { divisor = it },
                        label = { Text("Enter Divisor", color = Color.Black) },


                        placeholder = { Text("Enter binary (e.g.,1101 )", color = Color.Black) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),

                        modifier = Modifier.fillMaxWidth(),

                        textStyle = LocalTextStyle.current.copy(color = Color.Black),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.Black,
                            cursorColor = Color.Black,
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Calculate Button
                    Button(
                        onClick = {
                            if (dataString.isNotEmpty()) {
                                val (transmitted, crc) = encodeData(dataString, divisor)
                                transmittedData = transmitted
                                crcValue = crc
                                validationMessage = receiver(transmittedData, divisor)
                            } else {
                                showAlert = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text("Calculate CRC")
                    }

                    if (showAlert) {
                        AlertDialog(
                            onDismissRequest = { showAlert = false },
                            title = { Text(text = "Error") },
                            text = { Text("Please enter a valid string.") },
                            confirmButton = {
                                Button(onClick = { showAlert = false }) {
                                    Text("OK")
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Output: Transmitted Data, CRC Value, and Validation Message
                    Text(
                        text = "Transmitted Data: $transmittedData",
                        color = Color.Black,
                    )
                    Text(
                        text = "CRC Value: $crcValue",
                        color = Color.Black,
                    )
                    Text(
                        text = "Validation: $validationMessage",
                        color = Color.Black,
                    )
                }
            }
        }
    }
}
