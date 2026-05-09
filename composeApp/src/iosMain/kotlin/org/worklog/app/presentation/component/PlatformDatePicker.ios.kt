package org.worklog.app.presentation.component

import androidx.compose.runtime.Composable
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRectMake
import platform.Foundation.*
import platform.UIKit.*

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun PlatformDatePicker(
    show: Boolean,
    initialDate: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    if (!show) return

    val rootController =
        UIApplication.sharedApplication.keyWindow?.rootViewController
            ?: return

    val alert = UIAlertController.alertControllerWithTitle(
        title = "Select Date",
        message = "\n\n\n\n\n\n",
        preferredStyle = UIAlertControllerStyleAlert
    )

    val datePicker = UIDatePicker().apply {
        datePickerMode = UIDatePickerMode.UIDatePickerModeDate
        preferredDatePickerStyle = UIDatePickerStyle.UIDatePickerStyleWheels
        maximumDate = NSDate() // Prevent future dates

        // Parse initial date (yyyy-MM-dd)
        initialDate.takeIf { it.isNotBlank() }?.let { dateStr ->
            val formatter = NSDateFormatter().apply {
                dateFormat = "yyyy-MM-dd"
                locale = NSLocale.localeWithLocaleIdentifier("en_US_POSIX")
                timeZone = NSTimeZone.localTimeZone
            }

            formatter.dateFromString(dateStr)?.let {
                date = it
            }
        }
    }

    datePicker.setFrame(CGRectMake(0.0, 40.0, 270.0, 150.0))
    datePicker.translatesAutoresizingMaskIntoConstraints = false
    alert.view.addSubview(datePicker)

    NSLayoutConstraint.activateConstraints(
        listOf(
            datePicker.centerXAnchor.constraintEqualToAnchor(alert.view.centerXAnchor),
            datePicker.centerYAnchor.constraintEqualToAnchor(alert.view.centerYAnchor),
            datePicker.widthAnchor.constraintEqualToConstant(250.0),
            datePicker.heightAnchor.constraintEqualToConstant(150.0)
        )
    )

    alert.addAction(
        UIAlertAction.actionWithTitle(
            title = "OK",
            style = UIAlertActionStyleDefault
        ) {
            val formatter = NSDateFormatter().apply {
                dateFormat = "yyyy-MM-dd"
                locale = NSLocale.localeWithLocaleIdentifier("en_US_POSIX")
                timeZone = NSTimeZone.localTimeZone
            }

            val selectedDate = formatter.stringFromDate(datePicker.date)
            onConfirm(selectedDate)
        }
    )

    alert.addAction(
        UIAlertAction.actionWithTitle(
            title = "Cancel",
            style = UIAlertActionStyleCancel
        ) {
            onDismiss()
        }
    )

    rootController.presentViewController(
        alert,
        animated = true,
        completion = null
    )
}
