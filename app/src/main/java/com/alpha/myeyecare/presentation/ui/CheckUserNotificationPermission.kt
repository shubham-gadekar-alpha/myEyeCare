package com.alpha.myeyecare.presentation.ui

import android.Manifest
import android.os.Build
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CheckUserNotificationPermission(
    fromReminderDetailsScreen: Boolean,
    shouldCheckPermission: Boolean,
    permissionGranted: () -> Unit,
    onActionClick: () -> Unit
) {
    val context = LocalContext.current
    var showPermissionRationaleDialog by remember { mutableStateOf(false) }
    var showSettingsRedirectDialog by remember { mutableStateOf(false) }
    var permissionAlreadyRequested by remember { mutableStateOf(false) } // To avoid multiple requests

    if (shouldCheckPermission) {
        // Only handle POST_NOTIFICATIONS on Android 13 (API 33) and above, no need to check permissions below this version.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationPermissionState = rememberPermissionState(
                permission = Manifest.permission.POST_NOTIFICATIONS
            )

            LaunchedEffect(key1 = notificationPermissionState.status) {
                if (!notificationPermissionState.status.isGranted &&
                    !notificationPermissionState.status.shouldShowRationale &&
                    !permissionAlreadyRequested // Only request if not already granted and rationale not needed initially
                ) {
                    // This means permission has not been asked for yet or was denied with "Don't ask again"
                    // For a fresh launch, this is where we'd typically ask.
                    // If it was "Don't ask again", this won't show a dialog but will update the status.

                    if (fromReminderDetailsScreen) {
                        showSettingsRedirectDialog = true
                    } else {
                        notificationPermissionState.launchPermissionRequest()
                        permissionAlreadyRequested = true // Mark that we've initiated a request
                    }
                } else if (!notificationPermissionState.status.isGranted && notificationPermissionState.status.shouldShowRationale) {
                    // If rationale should be shown (user denied once without "Don't ask again")
                    // This typically means you should show your custom UI explaining why you need it
                    // and then trigger launchPermissionRequest() from a button in that UI.
                    // For this example, we'll trigger a dialog if rationale is needed.
                    showPermissionRationaleDialog = true
                } else if (!notificationPermissionState.status.isGranted && !notificationPermissionState.status.shouldShowRationale && permissionAlreadyRequested) {
                    // User denied with "Don't ask again" or system doesn't allow requesting again.
                    // Guide them to settings.
                    showSettingsRedirectDialog = true
                } else if (notificationPermissionState.status.isGranted) {
                    permissionGranted.invoke()
                }
            }

            if (showPermissionRationaleDialog) {
                AlertDialog(
                    onDismissRequest = {
                        onActionClick.invoke()
                        showPermissionRationaleDialog = false
                    },
                    title = {
                        Text("Notification Permission")
                    },
                    text = {
                        Text(
                            "To keep you updated with your alerts, our app needs permission to send you notifications."
                        )
                    },
                    confirmButton = {
                        Button(onClick = {
                            onActionClick.invoke()

                            notificationPermissionState.launchPermissionRequest()
                            showPermissionRationaleDialog = false
                            permissionAlreadyRequested = true
                        }) {
                            Text("Grant Permission")
                        }
                    },
                    dismissButton = {
                        Button(onClick = {
                            onActionClick.invoke()

                            showPermissionRationaleDialog = false
                        }) {
                            Text("Maybe Later")
                        }
                    }
                )
            }

            if (showSettingsRedirectDialog) {
                AlertDialog(
                    onDismissRequest = {
                        onActionClick.invoke()
                        showSettingsRedirectDialog = false
                    },
                    title = {
                        Text("Notification Permission Required")
                    },
                    text = {
                        Text("Notifications are currently disabled. Please enable them in app settings to receive respective notifications.")
                    },
                    confirmButton = {
                        Button(onClick = {
                            onActionClick.invoke()

                            val intent =
                                android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.data =
                                android.net.Uri.fromParts("package", context.packageName, null)
                            context.startActivity(intent)
                            showSettingsRedirectDialog = false
                        }) {
                            Text("Open Settings")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                onActionClick.invoke()

                                showSettingsRedirectDialog = false
                            }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
        } else {
            permissionGranted.invoke()
        }
    }
}
