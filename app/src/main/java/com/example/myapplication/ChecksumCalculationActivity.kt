package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.myapplication.ui.theme.MyApplicationTheme

class ChecksumCalculationActivity : ComponentActivity() {

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
                                title = "Cheacksum generation ",
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
                        ChecksumApp(PaddingValues)
                    }
                }
            }
        }
    }
}

@Composable
fun ChecksumApp(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(paddingValues) // Apply padding values from Scaffold to avoid overlap
    ) {

        var sentMessage by rememberSaveable { mutableStateOf("") }
        var kBlocks by rememberSaveable { mutableStateOf("") }
        var blockSize by rememberSaveable { mutableStateOf("") }
        var result by rememberSaveable { mutableStateOf("") }
        var step1Result by rememberSaveable { mutableStateOf("") }
        var step2Result by rememberSaveable { mutableStateOf("") }
        var step3Result by rememberSaveable { mutableStateOf("") }
        var step4Result by rememberSaveable { mutableStateOf("") }
        var error by rememberSaveable { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = sentMessage,
                onValueChange = { sentMessage = it },
                label = { Text("Enter Sent Message", color = Color.Black) },  // Hint text in black


                placeholder = { Text("Enter binary (e.g.,10110110)", color = Color.Black) },

                isError = error && sentMessage.isEmpty(),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = kBlocks,
                onValueChange = { kBlocks = it },
                label = { Text("Enter number of blocks (k)", color = Color.Black) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = error && kBlocks.isEmpty(),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = blockSize,
                onValueChange = { blockSize = it },
                label = { Text("Enter Block Size (n bits)", color = Color.Black) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = error && blockSize.isEmpty(),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (sentMessage.isEmpty() || blockSize.isEmpty() || kBlocks.isEmpty()) {
                        error = true
                    } else {
                        error = false
                        val k = kBlocks.toInt()
                        val n = blockSize.toInt()

                        // 1) Break message into k blocks of n bits
                        val blocks = breakIntoBlocks(sentMessage, k, n)
                        step1Result = "Step 1: Blocks: $blocks"

                        // 2) Sum all k blocks
                        val summedBlocks = sumBlocks(blocks, n)
                        step2Result = "Step 2: Sum of blocks: $summedBlocks"

                        // 3) Add carry to the sum
                        val sumWithCarry = addCarry(summedBlocks, n)
                        step3Result = "Step 3: Sum after adding carry: $sumWithCarry"

                        // 4) Perform one's complement
                        val checksumResult = onesComplement(sumWithCarry)
                        step4Result = "Step 4: Checksum (One's complement): $checksumResult"

                        result = checksumResult
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Calculate Checksum")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display the steps
            Text(text = step1Result, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = step2Result, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = step3Result, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = step4Result, color = Color.Black)
            Spacer(modifier = Modifier.height(16.dp))

            // Display final checksum result
            Text(
                text = "Final Checksum: $result",
                style = MaterialTheme.typography.h5,
                color = Color.Black
            )
        }
    }
}

// Function to break the message into k blocks of n bits
fun breakIntoBlocks(data: String, k: Int, n: Int): List<String> {
    val blocks = mutableListOf<String>()
    var paddedData = data

    // Padding the data if necessary to fit the block size
    val requiredLength = k * n
    if (paddedData.length < requiredLength) {
        paddedData = "0".repeat(requiredLength - paddedData.length) + paddedData
    }

    for (i in 0 until k) {
        blocks.add(paddedData.substring(i * n, (i + 1) * n))
    }
    return blocks
}

// Function to sum all k blocks with carry handling
fun sumBlocks(blocks: List<String>, blockSize: Int): String {
    var sum = blocks[0]
    for (i in 1 until blocks.size) {
        sum = binaryAdd(sum, blocks[i], blockSize)
    }
    return sum
}

// Function to add binary numbers with carry handling
fun binaryAdd(a: String, b: String, blockSize: Int): String {
    var result = ""
    var carry = 0
    for (i in a.indices.reversed()) {
        val sum = (a[i] - '0') + (b[i] - '0') + carry
        carry = sum / 2
        result = (sum % 2).toString() + result
    }

    // If there is a carry, we add it to the result
    if (carry == 1) {
        result = binaryAdd(result, "1".padStart(blockSize, '0'), blockSize)
    }

    // Trim the result to the block size (to avoid overflow)
    return result.takeLast(blockSize)
}

// Function to add carry to the sum if any
fun addCarry(sum: String, blockSize: Int): String {
    // If there's overflow beyond blockSize, sum it back
    val carryBits = sum.length - blockSize
    return if (carryBits > 0) {
        val carryPart = sum.take(carryBits)
        val remainingPart = sum.takeLast(blockSize)
        binaryAdd(remainingPart, carryPart.padStart(blockSize, '0'), blockSize)
    } else {
        sum
    }
}

// Function to perform one's complement
fun onesComplement(data: String): String {
    return data.map { if (it == '0') '1' else '0' }.joinToString("")
}
