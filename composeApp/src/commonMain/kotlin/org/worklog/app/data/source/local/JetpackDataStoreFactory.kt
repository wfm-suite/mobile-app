package org.worklog.app.data.source.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

expect class JetpackDataStoreFactory {
    fun createDataStore(): DataStore<Preferences>
}