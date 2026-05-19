import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.hilt)
}

// Charge local.properties (jamais committe) pour les secrets et URLs
val localProperties = Properties().apply {
    val localPropsFile = rootProject.file("local.properties")
    if (localPropsFile.exists()) {
        load(FileInputStream(localPropsFile))
    }
}
fun localProp(key: String, default: String = ""): String =
    localProperties.getProperty(key) ?: System.getenv(key) ?: default

android {
    namespace = "com.karibou.pubia"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.karibou.pubia"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }

        // Secrets injectes dans BuildConfig — l'app les lit via BuildConfig.X
        buildConfigField(
            "String",
            "API_SHARED_SECRET",
            "\"${localProp("API_SHARED_SECRET", "dev-secret-replace-me")}\""
        )
        buildConfigField(
            "String",
            "BACKEND_BASE_URL",
            "\"${localProp("BACKEND_BASE_URL", "http://10.0.2.2:3000/")}\""
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" }
    }
}

dependencies {
    // Core + Lifecycle + Activity Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Reseau (preparation Phase 3, configure des Phase 1)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.kotlinx.coroutines.android)

    // Stockage (preparation Phase 2)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.security.crypto)

    // Images & video
    implementation(libs.coil.compose)
    implementation(libs.androidx.exifinterface)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)

    // Debug Compose
    debugImplementation(libs.androidx.ui.tooling)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
