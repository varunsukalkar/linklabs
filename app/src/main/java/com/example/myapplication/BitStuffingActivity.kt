package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.myapplication.ui.theme.MyApplicationTheme

class BitStuffingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val scaffoldState = rememberScaffoldState()
                    Scaffold(
                        scaffoldState = scaffoldState,
                        topBar = {
                            AppBar(
                                title = "Bit Stuffing",
                                onNavigationIconClick = {
                                    onBackPressed() // Handle back navigation
                                }
                            )
                        },
                        floatingActionButton = {
                            HelpDialogFAB(
                                explanationText = """
        Bit Stuffing Overview:
        Bit stuffing is a method used in data communication protocols to ensure data integrity by inserting non-information bits into a data stream. This technique is mainly used to prevent confusion between control signals and data.

        How to Use:
        1. Enter the original data stream in the input field.
        2. Click 'Calculate' to see the bit stuffing process in action.
        3. The app will show how the extra bits are inserted and explain how they prevent errors.

        Use Case:
        Bit stuffing is used in protocols like HDLC and CAN to distinguish between actual data and control information, ensuring smooth data communication.
    """.trimIndent(),

                            )
                        },
                    ) { PaddingValues ->
                        BitStuffingApp(PaddingValues())
                    }
                }
            }
        }
    }

    @Composable
    fun BitStuffingApp(paddingValues: PaddingValues) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues) // Apply padding values from Scaffold to avoid overlap
        ) {


            var bitStuffingInput by rememberSaveable { mutableStateOf("") }
            var stuffedOutput by rememberSaveable { mutableStateOf("") }
            var bitDestuffingInput by rememberSaveable { mutableStateOf("") }
            var destuffedOutput by rememberSaveable { mutableStateOf("") }
            var stuffingSteps by rememberSaveable { mutableStateOf(listOf<String>()) }
            var destuffingSteps by rememberSaveable { mutableStateOf(listOf<String>()) }

            // Bit stuffing logic with steps
            fun bitStuffing(arr: IntArray): Pair<IntArray, List<String>> {
                val brr = mutableListOf<Int>()
                val steps = mutableListOf<String>()
                var i = 0
                while (i < arr.size) {
                    if (arr[i] == 1) {
                        var count = 1
                        brr.add(arr[i])
                        steps.add("Bit 1 found at position $i, count: $count")
                        for (k in i + 1 until arr.size) {
                            if (arr[k] == 1 && count < 5) {
                                brr.add(arr[k])
                                count++
                                steps.add("Bit 1 found at position $k, count: $count")
                                if (count == 5) {
                                    brr.add(0)  // Insert 0 after 5 consecutive 1's
                                    steps.add("5 consecutive 1's found, inserting 0")
                                }
                            } else {
                                i = k - 1
                                break
                            }
                        }
                    } else {
                        brr.add(arr[i])
                        steps.add("Bit 0 found at position $i")
                    }
                    i++
                }
                return Pair(brr.toIntArray(), steps)
            }

            // Bit destuffing logic with steps
            fun bitDestuffing(arr: IntArray): Pair<IntArray, List<String>> {
                val brr = mutableListOf<Int>()
                val steps = mutableListOf<String>()
                var i = 0
                var count = 1
                while (i < arr.size) {
                    if (arr[i] == 1) {
                        brr.add(arr[i])
                        steps.add("Bit 1 found at position $i, count: $count")
                        for (k in i + 1 until arr.size) {
                            if (arr[k] == 1 && count < 5) {
                                brr.add(arr[k])
                                count++
                                steps.add("Bit 1 found at position $k, count: $count")
                                if (count == 5) {
                                    steps.add("5 consecutive 1's found, removing 0")
                                    i = k + 1 // Skip the next bit after 5 consecutive 1's
                                    break
                                }
                            } else {
                                i = k - 1
                                break
                            }
                        }
                    } else {
                        brr.add(arr[i])
                        steps.add("Bit 0 found at position $i")
                    }
                    i++
                }
                return Pair(brr.toIntArray(), steps)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(Color.White)
            ) {
                OutlinedTextField(
                    value = bitStuffingInput,
                    onValueChange = { bitStuffingInput = it },
                    label = { Text("Enter Binary Sequence for Stuffing", color = Color.Black) },
                    placeholder = { Text("Enter binary (e.g., 101010)", color = Color.Black) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .background(Color.White),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black,
                        textColor = Color.Black
                    )
                )

                Button(
                    onClick = {
                        val binaryArray =
                            bitStuffingInput.map { it.toString().toInt() }.toIntArray()
                        val (stuffed, steps) = bitStuffing(binaryArray)
                        stuffedOutput = stuffed.joinToString("")
                        stuffingSteps = steps
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)
                ) {
                    Text("Perform Bit Stuffing", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    item {
                        Text(text = "Stuffed Output: $stuffedOutput", color = Color.Black)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Steps for Bit Stuffing:", fontSize = 18.sp, color = Color.Black)
                    }
                    items(stuffingSteps.size) { index ->
                        Text(text = stuffingSteps[index], color = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = bitDestuffingInput,
                    onValueChange = { bitDestuffingInput = it },
                    label = { Text("Enter Binary Sequence for Destuffing", color = Color.Black) },
                    placeholder = { Text("Enter stuffed binary", color = Color.Black) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .background(Color.White),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black,
                        textColor = Color.Black
                    )
                )

                Button(
                    onClick = {
                        val stuffedArray =
                            bitDestuffingInput.map { it.toString().toInt() }.toIntArray()
                        val (destuffed, steps) = bitDestuffing(stuffedArray)
                        destuffedOutput = destuffed.joinToString("")
                        destuffingSteps = steps
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)
                ) {
                    Text("Perform Bit Destuffing", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    item {
                        Text(text = "Destuffed Output: $destuffedOutput", color = Color.Black)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Steps for Bit Destuffing:", fontSize = 18.sp, color = Color.Black)
                    }
                    items(destuffingSteps.size) { index ->
                        Text(text = destuffingSteps[index], color = Color.Black)
                    }
                }
            }
        }
    }
}


