plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}
val smoothVersionCode = providers.gradleProperty("smoothVersionCode").get().toInt()
val smoothVersionName = providers.gradleProperty("smoothVersionName").get()

android {
    namespace = "com.theemrock.smooth"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.theemrock.smooth"
        minSdk = 33
        targetSdk = 35
        versionCode = smoothVersionCode
        versionName = smoothVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("smoothRelease") {
            val signingKeys = listOf(
                "SMOOTH_SIGNING_STORE_FILE",
                "SMOOTH_SIGNING_STORE_PASSWORD",
                "SMOOTH_SIGNING_KEY_ALIAS",
                "SMOOTH_SIGNING_KEY_PASSWORD"
            )
            val signingValues = signingKeys.associateWith {
                providers.environmentVariable(it).orNull
            }
            val configured = signingValues.values.any { !it.isNullOrBlank() }

            if (configured) {
                signingKeys.forEach { name ->
                    require(!signingValues[name].isNullOrBlank()) {
                        "Missing signing environment variable: $name"
                    }
                }
                val path = signingValues.getValue("SMOOTH_SIGNING_STORE_FILE")!!
                require(file(path).isFile) {
                    "Release keystore file not found"
                }
                storeFile = file(path)
                storePassword = signingValues.getValue("SMOOTH_SIGNING_STORE_PASSWORD")
                keyAlias = signingValues.getValue("SMOOTH_SIGNING_KEY_ALIAS")
                keyPassword = signingValues.getValue("SMOOTH_SIGNING_KEY_PASSWORD")
            }
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("smoothRelease")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}
