package com.example.geoquizbook

import androidx.annotation.StringRes

data class Question(@StringRes val questionResId: Int, val answer: Boolean) {
}