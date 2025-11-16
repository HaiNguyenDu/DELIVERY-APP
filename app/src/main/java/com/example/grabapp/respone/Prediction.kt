package com.example.grabapp.respone

import com.example.grabapp.model.Address

data class Prediction(
    val description: String?,
    val matched_substrings: List<Any>?,
    val place_id: String?,
    val reference: String?,
    val structured_formatting: StructuredFormatting?,
    val has_children: Boolean?,
    val plus_code: PlusCode?,
    val compound: Compound?,
    val deprecated_description: String?,
    val terms: List<Term>?,
    val types: List<String>?,
    val distance_meters: Double?
)