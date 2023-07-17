package com.example.gptapi.models.imageresponse

data class GenerateImageModel(
    val created: Int,
    val `data`: List<Data>
)