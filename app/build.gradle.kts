plugins {
    // Modern plugin versions
    id("com.android.application") version "8.0.2"
    id("org.jetbrains.kotlin.android") version "1.8.21"
}

android {
    namespace = "com.gpn"
    compileSdk = 33 // Preview SDK; ensure you've installed "Android API 35 (Preview)" in SDK Manager

    defaultConfig {
        applicationId = "com.gpn"
        minSdk = 28
        targetSdk = 33 // Also a preview
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Enable Compose
    buildFeatures {
        compose = true
    }

    // Use a modern Compose Compiler extension that works with Kotlin 1.8.x
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
        // Remove kotlinCompilerVersion; it's no longer needed
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation("androidx.compose.material3:material3:1.0.0") // Update to latest stable version

    // Use the Compose BOM to keep versions in sync
    implementation(platform("androidx.compose:compose-bom:2023.05.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Core
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")

    // Retrofit for Network calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // (Optional) Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")

    // Lifecycle ViewModel for Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Compose Testing
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.05.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
