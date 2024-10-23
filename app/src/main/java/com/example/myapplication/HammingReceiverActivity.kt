package com.example.myapplication


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.sp
import kotlin.math.ceil
import kotlin.math.log2
import kotlin.math.pow
class HammingReceiverActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
//           HammingCodeErrorDetectionUI();
            Surface(
                modifier = Modifier.fillMaxSize(),
                color=Color.White
            ) {
                val scaffoldState = rememberScaffoldState()
                Scaffold(
                    scaffoldState = scaffoldState,
                    topBar = {
                        AppBar(
                            title = "Hamming code error detection ",
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
                    HammingCodeErrorDetectionUI(PaddingValues)
                }
            }

        }
    }
}

@Composable
fun HammingCodeErrorDetectionUI(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues) // Apply padding values from Scaffold to avoid overlap
    ) {
        var input by remember { mutableStateOf(TextFieldValue("")) }
        var resultMessage by rememberSaveable { mutableStateOf("") }
        var correctedMessage by rememberSaveable { mutableStateOf("") }
        var errorPosition by rememberSaveable { mutableStateOf(-1) }
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color.White)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "Hamming Code Error Detection",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Custom OutlinedTextField for Data
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text("Enter Data String", color = Color.Black) },


                placeholder = { Text("Enter binary (e.g.,1110101 )", color = Color.Black) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),


                textStyle = TextStyle(color = Color.Black, fontSize = 18.sp),
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Button to process the string
            Button(onClick = {
                val inputData = input.text
                val result = detectError(inputData)
                errorPosition = result.first
                correctedMessage = result.second
                resultMessage = result.third
            }) {
                Text("Detect Error")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Result display section
            if (errorPosition != -1) {
                Text(
                    text = "Error detected at bit: $errorPosition",
                    fontSize = 18.sp,
                    color = Color.Red
                )
                Text(
                    text = "Corrected Message: $correctedMessage",
                    fontSize = 18.sp,
                    color = Color.Green
                )
            } else {
                Text(text = "No errors detected.", fontSize = 18.sp, color = Color.Green)
            }

            // Steps explanation
            if (resultMessage.isNotEmpty()) {
                Text(
                    text = resultMessage,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(Color.White)
                        .padding(8.dp)
                )
            }
        }
    }
}
// Function to detect error in Hamming code
fun detectError(input: String): Triple<Int, String, String> {
    val bitmap = mapOf('1' to 1, '0' to 0)
    val bits = input.map { bitmap[it] ?: 0 }
    val positions = bits.indices.filter { bits[it] == 1 }

    val xorResult = positions.reduceOrNull { acc, i -> acc xor i } ?: 0
    val resultMessage = StringBuilder("Entered Message: ${bits.joinToString("")}\n")

    return if (xorResult == 0) {
        Triple(-1, input, "[INFO] No errors detected.")
    } else {
        val errorPosition = xorResult
        val correctedBits = bits.toMutableList().apply { this[errorPosition] = if (this[errorPosition] == 1) 0 else 1 }
        resultMessage.append("[INFO] Error detected at bit position ${errorPosition + 1} from left.\n")
        resultMessage.append("[SUCCESS] Error fixed. Corrected message is ${correctedBits.joinToString("")}.\n")
        Triple(errorPosition + 1, correctedBits.joinToString(""), resultMessage.toString())
    }
}

fun evenParity(vararg bits: Int): Int {
    return bits.sum() % 2
}

// Function to check Hamming Code and detect errors for 7-bit or 14-bit Hamming code
fun checkReceivedHammingCode(receivedCode: String): Pair<Int, List<String>> {
    val n = receivedCode.length
    val steps = mutableListOf<String>()
    val bits = receivedCode.map { it.toString().toInt() }

    var errorPosition = 0

    if (n == 7) {
        // For 7-bit Hamming code
        val p1 = evenParity(bits[2], bits[4], bits[6]) // P1 depends on D3, D5, D7
        steps.add("P1 calculated from D3, D5, D7: $p1")

        val p2 = evenParity(bits[2], bits[5], bits[6]) // P2 depends on D3, D6, D7
        steps.add("P2 calculated from D3, D6, D7: $p2")

        val p4 = evenParity(bits[4], bits[5], bits[6]) // P4 depends on D5, D6, D7
        steps.add("P4 calculated from D5, D6, D7: $p4")

        // Error detection for 7-bit
        errorPosition = p1 * 1 + p2 * 2 + p4 * 4

    } else if (n == 14) {
        // For 14-bit Hamming code
        val p1 = evenParity(bits[2], bits[4], bits[6], bits[8], bits[10], bits[12]) // P1 depends on D3, D5, D7, D9, D11, D13
        steps.add("P1 calculated from D3, D5, D7, D9, D11, D13: $p1")

        val p2 = evenParity(bits[2], bits[5], bits[6], bits[9], bits[10], bits[13]) // P2 depends on D3, D6, D7, D10, D11, D14
        steps.add("P2 calculated from D3, D6, D7, D10, D11, D14: $p2")

        val p4 = evenParity(bits[4], bits[5], bits[6], bits[11], bits[12], bits[13]) // P4 depends on D5, D6, D7, D12, D13, D14
        steps.add("P4 calculated from D5, D6, D7, D12, D13, D14: $p4")

        val p8 = evenParity(bits[8], bits[9], bits[10], bits[11], bits[12], bits[13]) // P8 depends on D9, D10, D11, D12, D13, D14
        steps.add("P8 calculated from D9, D10, D11, D12, D13, D14: $p8")

        // Error detection for 14-bit
        errorPosition = p1 * 1 + p2 * 2 + p4 * 4 + p8 * 8
    }

    return Pair(errorPosition, steps)
}

// Function to calculate parity based on Hamming code algorithm
fun calculateParity(position: Int, data: IntArray): Int {
    var parity = 0
    for (i in data.indices) {
        if (((i + 1) and (position + 1)) != 0) {
            parity = parity xor data[i]
        }
    }
    return parity
}

// Function to find how many parity bits are needed
fun findNumberOfParityBits(n: Int): Int {
    var r = 0
    while (2.0.pow(r) < n + r + 1) {
        r++
    }
    return r
}

// Function to return the bits checked by each parity bit
fun getCheckedBitsForParity(position: Int, length: Int): List<Int> {
    val checkedBits = mutableListOf<Int>()
    for (i in 0 until length) {
        if (((i + 1) and (position + 1)) != 0) {
            checkedBits.add(i + 1)
        }
    }
    return checkedBits
}
