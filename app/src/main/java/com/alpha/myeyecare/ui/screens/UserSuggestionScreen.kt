package com.alpha.myeyecare.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RateReview // Or Feedback, Comment etc.
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alpha.myeyecare.viewModel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSuggestionScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit = {},
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var suggestion by remember { mutableStateOf("") }

    var isNameError by remember { mutableStateOf(false) }
    var isEmailError by remember { mutableStateOf(false) }
    var isSuggestionError by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current // For showing Toast or Snackbar
    val coroutineScope = rememberCoroutineScope() // For launching coroutines for Snackbar

    // Simple validation functions (can be made more sophisticated)
    fun validateName(): Boolean {
        isNameError = name.isBlank()
        return !isNameError
    }

    fun validateEmail(): Boolean {
        // Basic email validation, can be improved with regex
        isEmailError =
            email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
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
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    val isNameValid = validateName()
                    val isEmailValid = validateEmail()
                    val isSuggestionValid = validateSuggestion()

                    if (isNameValid && isEmailValid && isSuggestionValid) {
                        focusManager.clearFocus() // Hide keyboard
                        viewModel.submitSuggestion(name, email, suggestion)
                        // Optionally, show a success message (e.g., Toast or Snackbar)
                        // and navigate away or clear fields
                        coroutineScope.launch {
                            // Example: Show a toast (replace with Snackbar for better UX)
                            android.widget.Toast.makeText(
                                context,
                                "Thanks for the input. We will definitely consider your input and work on it",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                        // name = "" // Clear fields after successful submission
                        // email = ""
                        // suggestion = ""
                    } else {
                        // Optionally, show a general error message if any field is invalid
                        coroutineScope.launch {
                            android.widget.Toast.makeText(
                                context,
                                "Please correct the errors.",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                icon = { Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Submit") },
                text = { Text("Submit") }
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp) // Outer padding for the content
                .verticalScroll(rememberScrollState()), // Make content scrollable
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp) // Spacing between elements
        ) {
            Text(
                text = "If you have any suggestions or facing any issues in this app, please mention the same here, we'd love to hear your thoughts!",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Name Field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; if (isNameError) validateName() },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Your Name") },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = "Name Icon"
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                    capitalization = KeyboardCapitalization.Words
                ),
                singleLine = true,
                isError = isNameError,
                supportingText = { if (isNameError) Text("Name cannot be empty") }
            )

            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; if (isEmailError) validateEmail() },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Your Email") }, // Or make it mandatory
                leadingIcon = {
                    Icon(
                        Icons.Filled.Email,
                        contentDescription = "Email Icon"
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                isError = isEmailError,
                supportingText = { if (isEmailError) Text("Enter a valid email address") }
            )

            // Suggestion Field (Multi-line)
            OutlinedTextField(
                value = suggestion,
                onValueChange = { suggestion = it; if (isSuggestionError) validateSuggestion() },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 150.dp), // Make it taller
                label = { Text("Your Suggestion") },
                leadingIcon = { // Optional icon for suggestion field
                    Icon(
                        Icons.Filled.RateReview, // Or Icons.Filled.Feedback, Icons.Filled.Comment
                        contentDescription = "Suggestion Icon",
                        modifier = Modifier.padding(top = 12.dp) // Adjust icon padding if label is on top
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done, // Or ImeAction.Send if you want to submit from keyboard
                    capitalization = KeyboardCapitalization.Sentences
                ),
                keyboardActions = KeyboardActions(
                    onDone = { // Or onSend
                        focusManager.clearFocus()
                        // Optionally trigger submission here too, or just hide keyboard
                    }
                ),
                isError = isSuggestionError,
                supportingText = { if (isSuggestionError) Text("Suggestion cannot be empty") },
                maxLines = 8 // Allow multiple lines, but limit expansion somewhat
            )

            Spacer(modifier = Modifier.height(64.dp)) // Extra space so FAB doesn't overlap last field too much
        }
    }
}
