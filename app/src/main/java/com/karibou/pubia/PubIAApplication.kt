package com.karibou.pubia

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application racine — initialise Hilt pour l'injection de dependances.
 * Designe dans AndroidManifest.xml via android:name=".PubIAApplication".
 */
@HiltAndroidApp
class PubIAApplication : Application()
