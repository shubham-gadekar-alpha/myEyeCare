package com.alpha.myeyecare.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date


data class Suggestion(
    val id: String? = null,
    val name: String = "",
    val email: String = "",
    val text: String = "",
    @ServerTimestamp // Automatically sets the timestamp on the server
    val timestamp: Date? = null,
    val status: String = "new" // Optional: for tracking (e.g., new, reviewed, implemented)
) {
    // No-argument constructor is required by Firestore for deserialization
    constructor() : this(null, "", "", "", null, "new")
}
