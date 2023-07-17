package com.example.gptapi

data class MessageModel(
    var isUser : Boolean,
    var isImage : Boolean,
    var message : String
)
