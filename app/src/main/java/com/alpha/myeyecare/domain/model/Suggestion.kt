package com.alpha.myeyecare.domain.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Suggestion(
    val id: String? = null,
    val name: String = "",
    val email: String = "",
    val text: String = "",
    @ServerTimestamp
    val timestamp: Date? = null,
    val status: String = "new"
)
