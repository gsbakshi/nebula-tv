package com.example.nebula.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Typography

val Typography = Typography(
    // Used for section titles, panel headers
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Thin,
        fontSize = 40.sp,
        letterSpacing = (-1).sp,
    ),
    // Used for subsection headers
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Light,
        fontSize = 20.sp,
        letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        letterSpacing = 0.5.sp,
    ),
    // Body text
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Light,
        fontSize = 14.sp,
        letterSpacing = 0.25.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        letterSpacing = 0.25.sp,
        lineHeight = 19.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        letterSpacing = 0.4.sp,
    ),
    // Labels — tracked out for a precision instrument feel
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        letterSpacing = 2.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 9.sp,
        letterSpacing = 1.sp,
    ),
)
