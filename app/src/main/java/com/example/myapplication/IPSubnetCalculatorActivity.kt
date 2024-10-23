package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment

import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import java.util.*
class IPSubnetCalculatorActivity
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
                                title = "IP Subnet CalCulator",
                                onNavigationIconClick = {
                                    onBackPressed() // Handle back navigation
                                }
                            )
                        },
                        floatingActionButton = {
                            HelpDialogFAB(
                                explanationText = """
        IP Addressing Overview:
        IP addressing involves assigning unique addresses to devices on a network, allowing communication between them. Subnetting is the process of dividing a network into smaller, manageable sub-networks.

        How to Use:
        1. Enter an IP address (e.g., 192.168.0.1) in the input field.
        2. Enter a subnet mask in CIDR format (e.g., /24) or decimal format (e.g., 255.255.255.0).
        3. Click 'Calculate' to view detailed results, including the network address, broadcast address, first and last usable addresses, number of hosts, and class of the IP.
        4. The app will also display step-by-step calculations for subnetting and explain how each result is derived.

        Use Case:
        Understanding IP addressing and subnetting is critical for network engineers and administrators to efficiently manage networks, optimize traffic, and improve security.
        
     
    """.trimIndent(),

                            )
                        },
                    ) { PaddingValues ->
                        IPSubnetCalculatorUI(PaddingValues)
                    }
                }
            }
        }
    }
}
@Composable
fun IPSubnetCalculatorUI(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(paddingValues) // Apply padding values from Scaffold to avoid overlap
    ) {
        var ipAddress by rememberSaveable { mutableStateOf("") }
        var subnetMask by rememberSaveable { mutableStateOf("") }
        var calculationResult by rememberSaveable { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // IP Address Input
            OutlinedTextField(
                value = ipAddress,
                onValueChange = { ipAddress = it },
                label = { Text("Enter IP Address", color = Color.Black) },
                placeholder = { Text("Enter IP (e.g.,106.211.103.147 )", color = Color.Black) },

                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subnet Mask Input
            OutlinedTextField(
                value = subnetMask,
                onValueChange = { subnetMask = it },
                label = { Text("Enter Subnet Mask (e.g. /30)", color = Color.Black) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
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
                    calculationResult = calculateSubnet(ipAddress, subnetMask)
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Calculate")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Calculation Results Display
            if (calculationResult.isNotEmpty()) {
                Text(
                    text = calculationResult,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Left
                )
            }
        }
    }
}

fun calculateSubnet(ip: String, subnet: String): String {
    val prefix = subnet.removePrefix("/").toIntOrNull() ?: return "Invalid subnet mask"

    // Convert the IP address into 32-bit integer
    val ipParts = ip.split(".").map { it.toIntOrNull() }
    if (ipParts.size != 4 || ipParts.any { it == null || it !in 0..255 }) {
        return "Invalid IP address"
    }
    val ipInt = ipParts[0]!! shl 24 or (ipParts[1]!! shl 16) or (ipParts[2]!! shl 8) or ipParts[3]!!

    // Calculate subnet mask from the prefix (e.g., /25 -> 255.255.255.128)
    val subnetMaskInt = (0xFFFFFFFF shl (32 - prefix)).toInt()
    val subnetMask = intToIp(subnetMaskInt)

    // Calculate the network address
    val networkAddressInt = ipInt and subnetMaskInt
    val networkAddress = intToIp(networkAddressInt)

    // Calculate the broadcast address (invert subnet mask and OR with network address)
    val broadcastAddressInt = networkAddressInt or subnetMaskInt.inv()
    val broadcastAddress = intToIp(broadcastAddressInt)

    // First and Last usable addresses
    val firstUsableAddress = intToIp(networkAddressInt + 1)
    val lastUsableAddress = intToIp(broadcastAddressInt - 1)

    // Total usable hosts
    val totalHosts = (1 shl (32 - prefix)) - 2

    // Determine class
    val ipClass = when (ipParts[0]) {
        in 0..127 -> "Class A"
        in 128..191 -> "Class B"
        in 192..223 -> "Class C"
        in 224..239 -> "Class D"
        else -> "Class E"
    }

    // Return results and steps
    return """
        Subnet Mask: $subnetMask
        Network Address: $networkAddress
        First Usable Address: $firstUsableAddress
        Last Usable Address: $lastUsableAddress
        Broadcast Address: $broadcastAddress
        Total Usable Hosts: $totalHosts
        Class: $ipClass

        Calculation Steps:
        1. Subnet mask is derived from the prefix length ($prefix) -> $subnetMask.
        2. Network address is calculated by IP & Subnet Mask -> $networkAddress.
        3. Broadcast address is Network OR with inverted Subnet Mask -> $broadcastAddress.
        4. First usable address is Network + 1 -> $firstUsableAddress.
        5. Last usable address is Broadcast - 1 -> $lastUsableAddress.
        6. Total hosts are calculated as 2^(32 - prefix) - 2 -> $totalHosts.
    """.trimIndent()
}

// Utility function to convert 32-bit integer back to dotted decimal format
fun intToIp(ip: Int): String {
    return "${(ip shr 24) and 0xFF}.${(ip shr 16) and 0xFF}.${(ip shr 8) and 0xFF}.${ip and 0xFF}"
}