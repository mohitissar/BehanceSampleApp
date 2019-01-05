package com.futureworkshops.domain.extension

import android.app.Activity
import android.view.View


fun Activity.hideKeyboard() {
    hideKeyboard(if (currentFocus == null) View(this) else currentFocus)
}