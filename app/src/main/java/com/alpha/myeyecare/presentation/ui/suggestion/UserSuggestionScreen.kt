package com.alpha.myeyecare.presentation.ui.suggestion

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSuggestionScreen(
    viewModel: SuggestionSubmissionViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var suggestion by remember { mutableStateOf("") }

    var isNameError by remember { mutableStateOf(false) }
    var isEmailError by remember { mutableStateOf(false) }
    var isSuggestionError by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    fun validateName(): Boolean {
        isNameError = name.isBlank()
        return !isNameError
    }

    fun validateEmail(): Boolean {
        isEmailError = email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        return !isEmailError
    }

    fun validateSuggestion(): Boolean {
        isSuggestionError = suggestion.isBlank()
        return !isSuggestionError
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Submit Your Suggestion") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }, floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    val isNameValid = validateName()
                    val isEmailValid = validateEmail()
                    val isSuggestionValid = validateSuggestion()

                    if (isNameValid && isEmailValid && isSuggestionValid) {
                        focusManager.clearFocus()
                        viewModel.submitSuggestion(name, email, suggestion)
                        coroutineScope.launch {
                            Toast.makeText(
                                context,
                                "Thanks for the input. We will definitely consider your input and work on it",
                                Toast.LENGTH_SHORT
                            ).show()
                            delay(200)
                            onNavigateBack.invoke()
                        }
                    } else {
                        coroutineScope.launch {
                            Toast.makeText(
                                context, "Please correct the errors.", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                icon = { Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Submit") },
                text = { Text("Submit") }
            )
        }, floatingActionButtonPosition = FabPosition.Companion.Center
    ) { innerPadding ->
        Column(
            modifier = Modifier.Companion
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Companion.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "If you have any suggestions or facing any issues in this app, please mention the same here, we'd love to hear your thoughts!",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    if (isNameError) validateName()
                },
                modifier = Modifier.Companion.fillMaxWidth(),
                label = { Text("Your Name") },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Person, contentDescription = "Name Icon"
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Companion.Text,
                    imeAction = ImeAction.Companion.Next,
                    capitalization = KeyboardCapitalization.Companion.Words
                ),
                singleLine = true,
                isError = isNameError,
                supportingText = { if (isNameError) Text("Name cannot be empty") }
            )

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    if (isEmailError) validateEmail()
                },
                modifier = Modifier.Companion.fillMaxWidth(),
                label = { Text("Your Email") },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Email,
                        contentDescription = "Email Icon"
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Companion.Email,
                    imeAction = ImeAction.Companion.Next
                ),
                singleLine = true,
                isError = isEmailError,
                supportingText = { if (isEmailError) Text("Enter a valid email address") }
            )

            OutlinedTextField(
                value = suggestion,
                onValueChange = {
                    suggestion = it
                    if (isSuggestionError) validateSuggestion()
                },
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 150.dp),
                label = { Text("Your Suggestion") },
                leadingIcon = {
                    Icon(
                        Icons.Filled.RateReview,
                        contentDescription = "Suggestion Icon",
                        modifier = Modifier.Companion.padding(top = 12.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Companion.Text,
                    imeAction = ImeAction.Companion.Done,
                    capitalization = KeyboardCapitalization.Companion.Sentences
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                isError = isSuggestionError,
                supportingText = { if (isSuggestionError) Text("Suggestion cannot be empty") },
                maxLines = 8
            )

            Spacer(modifier = Modifier.Companion.height(64.dp))
        }
    }
}
