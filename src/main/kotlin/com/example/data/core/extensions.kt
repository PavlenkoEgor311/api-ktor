package com.example.data.core

import com.example.data.model.GlobalNote

fun GlobalNote.isValid(): Boolean {
    return (this.title.isNotEmpty()
            && this.description.isNotEmpty()
            && this.status.isNotEmpty()
            && this.date.isNotEmpty())
}