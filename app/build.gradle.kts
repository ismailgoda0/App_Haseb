plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.devtools.ksp)
}

android {
    namespace = "com.homecash"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.homecash"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "1.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    // Public Supabase project values only. Never put service_role keys in the Android app.
    val supabaseUrl = providers.gradleProperty("HOME_CASH_SUPABASE_URL").orElse("").get()
    val supabaseAnonKey = providers.gradleProperty("HOME_CASH_SUPABASE_ANON_KEY").orElse("").get()
    defaultConfig {
        buildConfigField("String", "SUPABASE_URL", "\"${supabaseUrl.replace("\\", "\\\\").replace("\"", "\\\"")}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${supabaseAnonKey.replace("\\", "\\\\").replace("\"", "\\\"")}\"")
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Security & DataStore
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.biometric)
    implementation(libs.android.database.sqlcipher)
    implementation(libs.androidx.sqlite.ktx)

    // Coroutines & Serialization & WorkManager
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.work.runtime.ktx)

    // Networking / Cloud Sync
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // Testing / Debug
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.robolectric)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
