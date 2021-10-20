package org.phantancy.fill_blank_text.entity

data class BlankEntity(
        val id: String,
        val tag: String,
        var defaultValue: String,
        var blankWidth: Int,
        var blankHeight: Int
)
