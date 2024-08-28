package com.example.composediary.ui.home


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composediary.R
import com.example.composediary.ui.theme.DarkBlue
import com.example.composediary.ui.theme.Gray
import com.example.composediary.ui.theme.Orange


@Composable
fun AddDiaryDialog(
    diaryText: String,
    onValueChanged: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { },
            text = {
                OutlinedTextField(
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Orange,   // 포커스 상태일 때의 테두리 색상
                        unfocusedBorderColor = Gray // 비포커스 상태일 때의 테두리 색상
                    ),
                    value = diaryText,
                    onValueChange = onValueChanged,
                    label = {
                        Text(
                            stringResource(R.string.write),
                            fontSize = 18.sp,
                            color = DarkBlue,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }, modifier = Modifier.height(180.dp)
                )

            },
            modifier = modifier,
            dismissButton = {

                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(R.string.cancel), color = Gray)
                }
            },
            confirmButton = {
                TextButton(onClick = onSave) {
                    Text(stringResource(R.string.save), color = Orange)
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

