package com.aman.permissionsandlocationpicker

fun String?.appendIfNotBlank(s: String) = if (this != null && isNotBlank()) "$this$s" else ""

