package com.app.swipe.model

data class SuccessResponse(
    val message: String,
    val product_details: Product,
    val product_id: Long,
    val success: Boolean
)
