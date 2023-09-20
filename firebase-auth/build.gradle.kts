@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidLibrary)
    kotlin("multiplatform")
    `maven-publish`
}

group = "com.susumunoda.firebase"
version = "1.0"

kotlin {
    androidTarget {
        publishLibraryVariants("release")

        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    ios()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines)
                implementation(libs.authcontroller)
                implementation(libs.gitlive.firebase.auth)
            }
        }
    }
}

android {
    namespace = "com.susumunoda.firebase.auth"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
