package org.worklog.app

import androidx.compose.ui.window.ComposeUIViewController
import org.worklog.app.core.di.initKoin
import org.worklog.app.presentation.app.App

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) {
    App()
}