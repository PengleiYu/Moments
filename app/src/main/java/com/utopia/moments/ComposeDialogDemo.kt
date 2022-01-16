package com.utopia.moments

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.utopia.moments.ui.theme.MomentsTheme

@Composable
fun DialogDemo(setShowDialog: (Boolean) -> Unit) {
  AlertDialog(
    onDismissRequest = {
    },
    title = {
      Text("Title")
    },
    confirmButton = {
      Button(
        onClick = {
          // Change the state to close the dialog
          setShowDialog(false)
        },
      ) {
        Text("Confirm")
      }
    },
    dismissButton = {
      Button(
        onClick = {
          // Change the state to close the dialog
          setShowDialog(false)
        },
      ) {
        Text("Dismiss")
      }
    },
    text = {
      Text("This is a text on the dialog")
    },
  )
}

@Preview(
  showSystemUi = true,
  showBackground = true,
)
@Composable
fun PreviewDialog() {
  MomentsTheme {
    DialogDemo(setShowDialog = {})
  }
}