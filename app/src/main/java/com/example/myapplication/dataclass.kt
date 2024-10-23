package com.example.myapplication

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

class dataclass (
     val painter: Int,
     val contentDescription: String,
     val title: String,
     val modifier: Modifier = Modifier
)
fun dummydata(): List<Any> {
     return listOf(
          dataclass(
               painter =R.drawable.q1,
               contentDescription = "I am Varun",
               title = "Checksum - checksum generation",
               modifier = Modifier
          ),
          dataclass(
               painter = R.drawable.q9,
               contentDescription = "I am Varun",
               title = "Checksum - Error Detection",
               modifier = Modifier
          ),
          dataclass(
               painter = R.drawable.q2,
               contentDescription = "I am Varun",
               title = "Hamming Code",
               modifier = Modifier
          ),
          dataclass(
               painter = R.drawable.q4,
               contentDescription = "I am Varun",
               title = "Hamming Code  - Receiver side",
               modifier = Modifier
          ),
          dataclass(
               painter = R.drawable.q3,
               contentDescription = "I am Varun",
               title = "IP Addressing and Subnetting Calculator",
               modifier = Modifier
          ),
          dataclass(
               painter = R.drawable.earth,
               contentDescription = "I am Varun",
               title = "Bit Stuffing",
               modifier = Modifier
          ),
          dataclass(
               painter  = R.drawable.q8,
               contentDescription = "I am Varun",
               title =
               "Cyclic redundancy check-2",

               modifier = Modifier
          ),

          dataclass(
               painter =R.drawable.q5,
               contentDescription = "I am Varun",
               title = "Parity Bit Calculation",
               modifier = Modifier
          ),



     )
}




