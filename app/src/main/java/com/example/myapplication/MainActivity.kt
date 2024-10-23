package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*


import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.ui.theme.MyApplicationTheme

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavDestination.Companion.hierarchy


import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.launch


class MainActivity : androidx.activity.ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                var showAboutDialog by remember { mutableStateOf(false) }
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color=Color.White
                ) {
                    val scaffoldState = rememberScaffoldState()
                    val scope = rememberCoroutineScope()
                    Scaffold(
                        scaffoldState = scaffoldState,
                        topBar = {
                            AppBar(
                                onNavigationIconClick = {
                                    scope.launch {
                                        scaffoldState.drawerState.open()
                                    }
                                }
                            )
                        },
                        drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
                        drawerContent = {
                            DrawerHeader()
                            DrawerBody(
                                items = listOf(
                                    MenuItem(
                                        id = "home",
                                        title = "Feedback",
                                        contentDescription = "Go to home screen",
                                        icon = Icons.Default.Home
                                    ),

                                    MenuItem(
                                        id = "help",
                                        title = "About",
                                        contentDescription = "Get help",
                                        icon = Icons.Default.Info
                                    ),
                                ),
                                onItemClick = {
                                    println("Clicked on ${it.title}")
                                    if(it.title=="About"){
                                        showAboutDialog = true ;


                                        }
                                    if(it.title=="Feedback"){
                                        sendFeedback(context)

                                    }



                                }
                            )
                        }
                    ) {paddingview->


                        MainScreen(paddingview);
                    }
                    if (showAboutDialog) {
                        AboutDialog(onDismiss = { showAboutDialog = false })
                    }
    }
}



}
            }

    fun sendFeedback(context: android.content.Context) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // Only email apps should handle this
            putExtra(Intent.EXTRA_EMAIL, arrayOf("sukalkarvp@rknec.edu")) // Recipient email
            putExtra(Intent.EXTRA_SUBJECT, "Feedback for Your App") // Subject line
            putExtra(Intent.EXTRA_TEXT, "Hi, I would like to provide the following feedback...") // Optional body text
        }

        try {
            // Use Intent.createChooser to safely open the email app chooser
            context.startActivity(Intent.createChooser(intent, "Send Feedback"))
        } catch (e: Exception) {
            // If there's no email app available, show a toast message
            Toast.makeText(context, "No email app available", Toast.LENGTH_SHORT).show()
        }
    }
    @Composable
    fun AboutDialog(onDismiss: () -> Unit) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Box(
                modifier = Modifier
                    .size(400.dp, 600.dp)
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp) // Padding for content inside dialog
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Close button at the top right
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        IconButton(onClick = { onDismiss() }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    // Title of the dialog
                    Text(
                        text = "About This App",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // App description or details
                    Text(
                        text = "This app provides tools for various network algorithms such as Checksum, Hamming Code, CRC, and more. It is designed to help users calculate and understand network protocols.",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Version or developer info
                    Text(
                        text = "Version: 1.0.0\n",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Additional information at the bottom
                    Text(
                        text = "Created by Varun Sukalkar under the guidance of Professor Devishree Naidu",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }


    @Composable
    fun Greeting(name: String) {
        Text(text = "Hello $name!")
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {




    }
    @Composable
    fun ColumnExample(numbers: List<dataclass>, onItemClicked: (dataclass) -> Unit) {
        LazyColumn(
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(numbers) { currentCount ->
                Varun(currentCount, onItemClicked)
            }
        }
    }


    @Composable
    fun Varun(
        dataclass: dataclass,
        onItemClicked: (dataclass) -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onItemClicked(dataclass) }  // Add the clickable modifier here
                .padding(8.dp),  // Optional: Padding around the card
            shape = RoundedCornerShape(15.dp),
            elevation = 15.dp
        ) {
            Box(modifier = Modifier.height(150.dp)) {
                Image(
                    painter = painterResource(id = dataclass.painter),
                    contentDescription = dataclass.contentDescription,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .matchParentSize()
                        .fillMaxHeight()
                        .fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black
                                ),
                                startY = 200f
                            )
                        )
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Text(
                        dataclass.title,
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    )
                }
            }
        }


    }

    // Bottom Navigation Item
    data class BottomNavItem(val name: String, val route: String, val icon: ImageVector)



    @Composable
    fun MainScreen(paddingValues: PaddingValues) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply padding values from Scaffold to avoid overlap
        ) {


            val navController = rememberNavController()

            Scaffold(
                floatingActionButton = {
                    // Show FAB only on HomeScreen
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    if (currentDestination?.hierarchy?.any { it.route == "home" } == true) {
                        FloatingActionButton(
                            onClick = {
                                navController.navigate("search")
                            },
                            backgroundColor = Color.White,
                            modifier = Modifier.size(80.dp)  // Adjust the FAB size here
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_1), // Replace with your image resource
                                contentDescription = "Search",
                                modifier = Modifier.size(74.dp)  // Adjust the icon size here
                            )
                        }
                    }
                }
            ) { innerPadding ->
                NavigationHost(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }

    @Composable
    fun NavigationHost(navController: NavHostController, modifier: Modifier = Modifier) {
        NavHost(navController, startDestination = "home", modifier = modifier) {
            composable("home") { HomeScreen() }
            composable("search") { SearchScreen() }
        }
    }


    @Composable
    fun HomeScreen() {
        val context = LocalContext.current // Get the current Android context

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column(Modifier.fillMaxHeight()) {
                ColumnExample(numbers = dummydata() as List<dataclass>) { selectedItem ->
                    when (selectedItem.title) {
                        "Checksum - checksum generation" -> {
                            val intent = Intent(context, ChecksumCalculationActivity::class.java)
                            context.startActivity(intent)
                        }
                        "Checksum - Error Detection" -> {
                            val intent = Intent(context, Checksum2Activity::class.java)
                            context.startActivity(intent)
                        }
                        "Hamming Code  - Receiver side" -> {
                            val intent = Intent(context,HammingReceiverActivity ::class.java)
                            context.startActivity(intent)
                        }
                        "Hamming Code" -> {
                            val intent = Intent(context, HammingCodeActivity::class.java)
                            context.startActivity(intent)
                        }
                        "IP Addressing and Subnetting Calculator" -> {
                            val intent = Intent(context, IPSubnetCalculatorActivity::class.java)
                            context.startActivity(intent)
                        }
                        "Bit Stuffing" -> {
                            val intent = Intent(context, BitStuffingActivity::class.java)
                            context.startActivity(intent)
                        }

                        "Cyclic redundancy check-2" -> {
                            val intent = Intent(context, CRC2Activity::class.java)
                            context.startActivity(intent)
                        }
                        "Parity Bit Calculation" -> {
                            val intent = Intent(context, ParityBitActivity::class.java)
                            context.startActivity(intent)
                        }

                        else -> {
                            // Handle any default case if necessary
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun SearchScreen() {
ChatBotUI()
}

@Composable
fun ProfileScreen() {

}




data class Command(val command: String, val description: String, val example: String)






@Composable
fun ChatBotUI() {


    var userInput by remember { mutableStateOf("") }
    var chatHistory by remember { mutableStateOf(listOf<String>()) }

    // This represents the response to commands; modify based on logic for different commands
    fun getCommandResponse(command: String): String {
        return when (command.trim().lowercase()) {
            "copy" -> "Usage: copy [source] [destination]\nCopies one or more files to another location.\nExample:\ncopy file.txt D:\\Backup\\\n\nExplanation: Copies 'file.txt' to the 'Backup' directory."
            "del" -> "Usage: del [file]\nDeletes one or more files.\nExample:\ndel file.txt\n\nExplanation: Deletes 'file.txt' from the current directory."
            "rename" -> "Usage: rename [oldname] [newname]\nRenames a file.\nExample:\nrename file.txt newfile.txt\n\nExplanation: Renames 'file.txt' to 'newfile.txt'."
            "xcopy" -> "Usage: xcopy [source] [destination] [options]\nCopies files and directories, including subdirectories.\nExample:\nxcopy C:\\Data D:\\Backup /S\n\nExplanation: Copies all files and subdirectories from 'C:\\Data' to 'D:\\Backup'."
            "find" -> "Usage: find [string] [filename]\nSearches for a text string in a file.\nExample:\nfind \"error\" log.txt\n\nExplanation: Searches for the word 'error' in the 'log.txt' file."
            "robocopy" -> "Usage: robocopy [source] [destination] [options]\nRobust file copying and syncing tool.\nExample:\nrobocopy C:\\Source D:\\Destination /MIR\n\nExplanation: Mirrors the 'Source' directory to the 'Destination' directory."
            "subst" -> "Usage: subst [drive] [path]\nMaps a local folder to a drive letter.\nExample:\nsubst Z: C:\\Users\\Documents\n\nExplanation: Maps the 'Documents' folder to drive 'Z:'."
            "attrib" -> "Usage: attrib [+/-attribute] [file/directory]\nChanges the attributes of a file or directory.\nExample:\nattrib -h -s file.txt\n\nExplanation: Removes the hidden and system attributes from 'file.txt'."
            "cipher" -> "Usage: cipher [/e | /d] [file/directory]\nEncrypts or decrypts files and directories.\nExample:\ncipher /e secret.txt\n\nExplanation: Encrypts 'secret.txt'."

            // Directory Navigation
            "cd" -> "Usage: cd [directory]\nChanges the current directory.\nExample:\ncd D:\\Projects\n\nExplanation: Navigates to the 'Projects' folder on drive D."
            "cd /" -> "Usage: cd /\nNavigates to the root directory.\nExample:\ncd /\n\nExplanation: Changes to the root of the current drive."
            "dir" -> "Usage: dir [directory]\nDisplays the contents of a directory.\nExample:\ndir\n\nExplanation: Lists all files and subdirectories in the current directory."
            "mkdir" -> "Usage: mkdir [directory]\nCreates a new directory.\nExample:\nmkdir new_folder\n\nExplanation: Creates a directory named 'new_folder'."
            "rmdir" -> "Usage: rmdir [directory]\nRemoves a directory.\nExample:\nrmdir old_folder\n\nExplanation: Deletes the 'old_folder' directory."
            "tree" -> "Usage: tree [directory]\nDisplays a graphical representation of the folder structure.\nExample:\ntree C:\\Users\n\nExplanation: Shows the folder structure under 'C:\\Users'."

            // Network Commands
            "ipconfig" -> "Usage: ipconfig\nDisplays TCP/IP network configuration.\nExample:\nipconfig /all\n\nExplanation: Shows detailed network settings."
            "ping" -> "Usage: ping [host]\nTests connectivity to a host.\nExample:\nping google.com\n\nExplanation: Sends packets to 'google.com' to test if it's reachable."
            "tracert" -> "Usage: tracert [host]\nTraces the route to a host.\nExample:\ntracert google.com\n\nExplanation: Traces the network path to 'google.com'."
            "netstat" -> "Usage: netstat\nDisplays network connections and statistics.\nExample:\nnetstat -an\n\nExplanation: Lists all active connections."
            "nslookup" -> "Usage: nslookup [domain]\nQueries DNS to get domain or IP mappings.\nExample:\nnslookup google.com\n\nExplanation: Gets the IP address for 'google.com'."
            "netsh" -> "Usage: netsh [command]\nConfigures network settings.\nExample:\nnetsh interface ip show config\n\nExplanation: Displays network configuration for all interfaces."
            "arp -a" -> "Usage: arp -a\nDisplays the ARP table.\nExample:\narp -a\n\nExplanation: Shows IP-to-MAC address mappings."
            "hostname" -> "Usage: hostname\nDisplays the computer’s network name.\nExample:\nhostname\n\nExplanation: Shows the current machine’s network name."
            "pathping" -> "Usage: pathping [host]\nCombines ping and tracert.\nExample:\npathping google.com\n\nExplanation: Tests network path and shows packet loss information."
            "getmac" -> "Usage: getmac\nDisplays the MAC address.\nExample:\ngetmac\n\nExplanation: Shows the MAC address of the network card."
            "nbtstat" -> "Usage: nbtstat [options]\nDisplays NetBIOS over TCP/IP information.\nExample:\nnbtstat -n\n\nExplanation: Shows NetBIOS names and their IP addresses."
            "shutdown /i" -> "Usage: shutdown /i\nShuts down a remote computer.\nExample:\nshutdown /i\n\nExplanation: Opens the GUI for remote shutdown."

            // System Information
            "systeminfo" -> "Usage: systeminfo\nDisplays detailed system information.\nExample:\nsysteminfo\n\nExplanation: Shows OS, hardware, and network configuration details."
            "winver" -> "Usage: winver\nDisplays the current Windows version.\nExample:\nwinver\n\nExplanation: Opens a window showing the version of Windows you’re running."
            "tasklist" -> "Usage: tasklist\nLists currently running processes.\nExample:\ntasklist\n\nExplanation: Displays all active processes with their Process IDs (PIDs)."
            "taskkill" -> "Usage: taskkill /PID [pid] /F\nTerminates a process.\nExample:\ntaskkill /PID 1234 /F\n\nExplanation: Forcefully kills the process with PID 1234."
            "wmic" -> "Usage: wmic [command]\nDisplays WMI information.\nExample:\nwmic cpu get name\n\nExplanation: Retrieves information about the CPU."

            // Disk Management
            "diskpart" -> "Usage: diskpart\nOpens the Disk Partition tool.\nExample:\ndiskpart\n\nExplanation: A powerful disk partition management tool."
            "chkdsk" -> "Usage: chkdsk [disk] /f\nChecks and repairs the file system.\nExample:\nchkdsk C: /f\n\nExplanation: Scans and fixes errors on the 'C' drive."
            "format" -> "Usage: format [drive] /FS:[filesystem]\nFormats a disk.\nExample:\nformat D: /FS:NTFS\n\nExplanation: Formats the D drive with the NTFS file system."
            "list disk" -> "Usage: list disk\nLists all available disks.\nExample:\nlist disk\n\nExplanation: Displays all disks connected to the system."
            "select disk" -> "Usage: select disk [number]\nSelects a disk for partition operations.\nExample:\nselect disk 1\n\nExplanation: Selects disk 1 for further actions."
            "clean" -> "Usage: clean\nRemoves all partitions from a selected disk.\nExample:\nclean\n\nExplanation: Wipes all data and partitions from the disk."

            else -> "Command not recognized. Try again."
        }
    }


    // Greeting Text and Robot Image
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Greeting Message with Robot Image
        Row(

            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp) // Fixed height for Row
                  ,

            verticalAlignment = Alignment.CenterVertically) {

            Image(
                painter = painterResource(id = R.drawable.img), // Add robot image in res/drawable folder
                contentDescription = "Robot",
                contentScale = ContentScale.FillBounds, // Ensure image fills its allocated space
                modifier = Modifier
                    .weight(0.4f) // 40% of width
                    .fillMaxHeight() // Full height of Row
                    .clip(RoundedCornerShape(8.dp)) // Optional: Rounded corners for better aesthetics
            )
            Spacer(modifier = Modifier.width(16.dp)) // Space between image and text
            Text(
                text = "Hello! I'm your chatbot. You can enter any Windows or Linux command and I'll explain it with an example.",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray,
                modifier = Modifier
                    .weight(0.6f) // 60% of width
                    .fillMaxHeight() // Full height of Row
                    .padding(end = 16.dp) // Optional: Padding on the end
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display Chat History
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(chatHistory) { message ->
                if (message.startsWith("User:")) {
                    // User's command (aligned to the right)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = message.removePrefix("User:"),
                            color = Color.Black,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .background(Color.White)
                                .padding(8.dp)
                        )
                    }
                } else {
                    // Chatbot's response (aligned to the left)
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = message,
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontFamily = FontFamily.Monospace, // Different font for the chatbot response
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // User Input and Send Button (like WhatsApp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            OutlinedTextField(
                value = userInput,
                onValueChange = { userInput= it },
                label = { Text("enter command", color = Color.Black) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F),

                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black
                )
            )
            IconButton(
                onClick = {
                    if (userInput.isNotBlank()) {
                        // Update chat history with the user's command
                        chatHistory = chatHistory + "User: $userInput"

                        // Get command response and add to chat history
                        val response = getCommandResponse(userInput)
                        chatHistory = chatHistory + response

                        // Clear the input field
                        userInput = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF25D366), shape = CircleShape) // WhatsApp green color
            ) {
                Image(
                    painter = painterResource(id = R.drawable.k), // Add send icon to res/drawable
                    contentDescription = "Send",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier

                        .fillMaxHeight()
                        .fillMaxSize()
                )
            }
        }
    }
}