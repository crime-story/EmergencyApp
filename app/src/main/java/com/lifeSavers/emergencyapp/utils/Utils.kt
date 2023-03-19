package com.lifeSavers.emergencyapp.utils

import android.app.Activity
import android.content.Intent

class Utils {
    fun shareButtonFunctionality(activity: Activity) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "LifeSavers Emergency App")
        val appUrl = "https://play.google.com/store/apps/details?id=com.lifeSavers.emergencyapp"
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Check out the LifeSavers Emergency App at:\n$appUrl"
        )
        activity.startActivity(Intent.createChooser(shareIntent, "Share the app"))
    }

    fun generatePassword(length: Int): String {
        val allowedChars = ('a'..'z') + ('A'..'Z') + ('0'..'9') + listOf(
            '!',
            '@',
            '#',
            '$',
            '%',
            '^',
            '&',
            '*',
            '(',
            ')',
            '-',
            '_',
            '+',
            '=',
            '[',
            ']',
            '{',
            '}',
            '|',
            ';',
            ':',
            '<',
            '>',
            ',',
            '.',
            '?',
            '/'
        )
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
}