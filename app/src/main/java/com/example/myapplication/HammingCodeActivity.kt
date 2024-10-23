package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*

import androidx.compose.material.*
import androidx.compose.runtime.Composable

import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
class HammingCodeActivity

    : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
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
                                title = "Hamming code generation ",
                                onNavigationIconClick = {
                                    onBackPressed() // Handle back navigation
                                }
                            )
                        },

                                floatingActionButton = {
                                    HelpDialogFAB(
                                        explanationText = """
        Hamming Code Overview:
        Hamming code is an error-correcting code that detects and corrects single-bit errors in data transmission. By introducing redundancy bits, this method identifies any error positions during the decoding process.

        How to Use:
        1. Enter your binary data in the input field. This is the original data you wish to transmit.
        2. Tap on 'Calculate' to generate the Hamming code. The app will display the code and the steps taken to produce it.
        3. The result will also show the position of any errors detected in the data.
        4. Explore the detailed explanation of how the parity bits are calculated and how the error correction process works.
        
        Use Case:
        This method is widely used in digital communication systems, like in computer memory error detection and in data transmission, to ensure data integrity. Understanding how Hamming code works is essential for learning about data transmission and error detection techniques.
    """.trimIndent(),

                                    )

                        },
















                    ) { PaddingValues ->
                        HammingCodeApp(PaddingValues)
                    }
                }
            }
        }
    }
}

@Composable
fun HammingCodeApp(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(paddingValues) // Apply padding values from Scaffold to avoid overlap
    ) {

        var dataString by rememberSaveable { mutableStateOf("") }
        var encodedData by rememberSaveable { mutableStateOf("") }
        var validationMessage by rememberSaveable { mutableStateOf("") }
        var showSteps by rememberSaveable { mutableStateOf(false) }
        var stepsList by rememberSaveable { mutableStateOf(listOf<String>()) } // List to hold steps

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Data Input Section for encoding
            OutlinedTextField(
                value = dataString,
                onValueChange = { dataString = it },
                label = { Text("Enter Data String", color = Color.Black) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),


                placeholder = { Text("Enter binary (e.g.,1011 )", color = Color.Black) },


                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Calculate Button
            Button(
                onClick = {
                    if (dataString.isNotEmpty()) {
                        val hamming = HammingWithSteps(dataString)
                        encodedData = hamming.getEncodedMessage()
                        validationMessage = hamming.receiver()
                        stepsList = hamming.getSteps() // Capture steps
                        showSteps = true // Enable step display
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(contentColor = Color.White)
            ) {
                Text("Calculate Hamming Code")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Output Section
            Text("Encoded Data: $encodedData", color = Color.Black)
            Text("Validation: $validationMessage", color = Color.Black)

            // Display steps if showSteps is true
            if (showSteps) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Steps Involved in Hamming Code Calculation:", color = Color.Black)

                // Loop through each step and display it
                stepsList.forEachIndexed { index, step ->
                    Text("${index + 1}. $step", color = Color.Black)
                }
            }
        }
    }
}

class HammingWithSteps(var data: String) {
    private var m = data.length
    private var r = 0
    private lateinit var msg: CharArray
    private val steps = mutableListOf<String>() // List to hold the steps

    init {
        data = data.reversed() // Reverse the data as in C++ logic
        calculateRedundantBits()
        initializeMsgArray()
        setRedundantBits()
    }

    private fun calculateRedundantBits() {
        var power = 1
        while (power < (m + r + 1)) {
            r++
            power *= 2
        }
        steps.add("Calculated $r redundant bits.")
    }

    private fun initializeMsgArray() {
        msg = CharArray(m + r + 1)
        var curr = 0
        for (i in 1..m + r) {
            if (i and (i - 1) != 0) {
                msg[i] = data[curr++]
            } else {
                msg[i] = 'n' // Placeholder for redundant bits
            }
        }
        steps.add("Placed redundant bits at positions: ${msg.indices.filter { it != 0 && (it and (it - 1)) == 0 }}")
    }

    private fun setRedundantBits() {
        var bit = 0
        var i = 1
        while (i <= m + r) {
            var count = 0
            for (j in i + 1..m + r) {
                if (j and (1 shl bit) != 0 && msg[j] == '1') {
                    count++
                }
            }
            msg[i] = if (count % 2 == 1) '1' else '0'
            steps.add("Redundant bit at position $i is set to ${msg[i]}.")
            bit++
            i *= 2
        }
    }

    fun getEncodedMessage(): String {
        val finalMessage = msg.slice(1..m + r).reversed().joinToString(" ")
        steps.add("Final encoded message: $finalMessage")
        return finalMessage
    }

    fun getSteps(): List<String> {
        return steps
    }

    fun receiver(): String {
        var ans = ""
        var bit = 0
        var i = 1
        while (i <= m + r) {
            var count = 0
            for (j in i + 1..m + r) {
                if (j and (1 shl bit) != 0 && msg[j] == '1') {
                    count++
                }
            }
            ans += if (count % 2 == 1) {
                if (msg[i] == '1') '0' else '1'
            } else {
                if (msg[i] == '0') '0' else '1'
            }
            bit++
            i *= 2
        }

        return if ('1' in ans) {
            val wrongBit = ans.reversed().toInt(2)
            "Error in bit $wrongBit"
        } else {
            "Correct data packet received"
        }
    }
}
