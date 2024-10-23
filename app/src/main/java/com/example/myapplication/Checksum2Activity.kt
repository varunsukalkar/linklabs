package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.saveable.rememberSaveable

class Checksum2Activity

    : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            Surface(
                modifier = Modifier.fillMaxSize(),

                color=Color.White
            ) {
                val scaffoldState = rememberScaffoldState()
                Scaffold(
                    scaffoldState = scaffoldState,
                    topBar = {
                        AppBar(
                            title = "Checksum Detection ",
                            onNavigationIconClick = {
                                onBackPressed() // Handle back navigation
                            }
                        )
                    },
                    floatingActionButton = {
                        HelpDialogFAB(
                            explanationText = """
        Checksum Overview:
        The checksum is a simple error-detection method used to verify the integrity of data sent over networks. It sums up data blocks and verifies the result at the receiver's end.

        How to Use:
        1. Enter the sender's data string.
        2. Click 'Calculate' to generate a checksum. The app will display step-by-step calculations.
        3. Input the receiver's string to compare and check for transmission errors.
        4. View the step-by-step process showing how the checksum is validated and what happens when an error is detected.

        Use Case:
        The checksum method ensures that data hasn't been corrupted during transmission, and it is often used in network communications.
    """.trimIndent(),

                            )
                    },

                    ) { PaddingValues ->
                    ChecksumApp2(PaddingValues)
                }
            }

        }
    }

    @Composable
    fun ChecksumApp2(paddingValues: PaddingValues) {
        Box(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(paddingValues) // Apply padding values from Scaffold to avoid overlap
        ) {
            var sentMessage by rememberSaveable { mutableStateOf("") }
            var receivedMessage by rememberSaveable { mutableStateOf("") }
            var blockSize by rememberSaveable { mutableStateOf("8") }
            var result by rememberSaveable { mutableStateOf("") }
            var stepDetails by rememberSaveable { mutableStateOf(listOf<String>()) }
            var binaryAdditionResult by rememberSaveable { mutableStateOf("") }

            val scrollState = rememberScrollState()

            // UI Elements
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                OutlinedTextField(
                    value = sentMessage,
                    onValueChange = { sentMessage = it },

                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Enter Binary Sequence ", color = Color.Black) },
                    placeholder = {
                        Text(
                            "Enter binary (e.g.,1010100100111001 )",
                            color = Color.Black
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Black, unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = receivedMessage,
                    onValueChange = { receivedMessage = it },
                    label = { Text("Enter Received Message") },

                    placeholder = {
                        Text(
                            "Enter binary (e.g.,1010100100111001 )",
                            color = Color.Black
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Black, unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = blockSize,
                    onValueChange = { blockSize = it },
                    label = { Text("Enter Block Size") },


                    placeholder = { Text("Enter number (e.g.,8 )", color = Color.Black) },


                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Black, unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    val blockSizeInt = blockSize.toIntOrNull() ?: 8
                    val (steps, checksumResult, binaryResult) = checkForErrors(
                        sentMessage,
                        receivedMessage,
                        blockSizeInt
                    )
                    stepDetails = steps
                    result = checksumResult
                    binaryAdditionResult = binaryResult
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Calculate Checksum")
                }

                Spacer(modifier = Modifier.height(16.dp))


                Text(text = "Binary Addition Result:", color = Color.Black)
                Text(text = binaryAdditionResult, color = Color.Black)

                Spacer(modifier = Modifier.height(16.dp))


                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Top
                ) {
                    for (step in stepDetails) {
                        Text(text = step, color = Color.Black)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Display result
                Text(
                    text = "Result: $result",
                    color = if (result == "No Error") Color.Green else Color.Red
                )
            }
        }
    }

    // Function to handle the checksum error checking and binary addition
    fun checkForErrors(
        sentMessage: String,
        receivedMessage: String,
        blockSize: Int
    ): Triple<List<String>, String, String> {
        val steps = mutableListOf<String>()
        val checksum = calculateChecksum(sentMessage, blockSize)
        steps.add("Step 1: Checksum of the sent message is $checksum")

        // Split the received message into blocks based on the block size
        val blocks = receivedMessage.chunked(blockSize)
        steps.add("Step 2: Split received message into blocks: $blocks")

        // Add the blocks together
        var sum = blocks.first()
        for (block in blocks.drop(1)) {
            sum = binaryAddition(sum, block)
        }
        steps.add("Step 3: Sum of blocks is $sum")

        // Add the checksum to the sum of blocks
        val finalSum = binaryAddition(sum, checksum)
        steps.add("Step 4: Adding checksum to the sum gives $finalSum")

        // Perform one's complement
        val onesComplement = onesComplement1(finalSum)
        steps.add("Step 5: One's complement of the result is $onesComplement")

        val result = if (onesComplement.all { it == '0' }) {
            "No Error"
        } else {
            "Error Detected"
        }

        return Triple(steps, result, finalSum)
    }

    // Helper functions (binary addition, one's complement)
    fun binaryAddition(a: String, b: String): String {
        val maxLength = maxOf(a.length, b.length)
        val aPadded = a.padStart(maxLength, '0')
        val bPadded = b.padStart(maxLength, '0')

        var carry = 0
        var result = ""
        for (i in maxLength - 1 downTo 0) {
            val sum = aPadded[i].digitToInt() + bPadded[i].digitToInt() + carry
            result = (sum % 2).toString() + result
            carry = sum / 2
        }
        if (carry > 0) {
            result = carry.toString() + result
        }
        return result
    }

    fun onesComplement1(binaryString: String): String {
        return binaryString.map { if (it == '0') '1' else '0' }.joinToString("")
    }

    fun calculateChecksum(message: String, blockSize: Int): String {
        // Implement checksum calculation based on the sentMessage
        val blocks = message.chunked(blockSize)
        var sum = blocks.first()
        for (block in blocks.drop(1)) {
            sum = binaryAddition(sum, block)
        }
        return onesComplement1(sum)
    }

}