import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs")
    id("com.google.dagger.hilt.android")
    id("kotlinx-serialization")
}

val localProperties = Properties()
localProperties.load(FileInputStream(rootProject.file("local.properties")))

android {
    namespace = "com.w36495.senty"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.w36495.senty"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "DATABASE_BASE_URL", localProperties.getProperty("DATABASE_BASE_URL"))
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
    kotlinOptions {
//        freeCompilerArgs = ["-Xjvm-default=all-compatibility"]
        jvmTarget = "17"
    }

    buildFeatures {
        dataBinding = true
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {
    val lifecycle_version = "2.4.0"

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")

    implementation("androidx.cardview:cardview:1.0.0")

    implementation("de.hdodenhof:circleimageview:3.1.0") // Image Circle

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version") // viewModel
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")  // liveData

    implementation("androidx.viewpager2:viewpager2:1.0.0")  // viewpager2

    // Firebase
    val firebaseBom = platform("com.google.firebase:firebase-bom:32.3.1")
    implementation(firebaseBom)

    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.firebaseui:firebase-ui-auth:7.2.0")      // firebase - Authentication
    implementation("com.google.android.gms:play-services-auth:20.6.0")
    implementation("com.google.firebase:firebase-database")      // firebase - Realtime Database
    implementation("com.google.firebase:firebase-storage-ktx")   // firebase - Cloud Storage
    implementation("com.firebaseui:firebase-ui-storage:7.2.0")   // firebase - StorageUI

//    implementation 'com.google.android.gms:play-services-ads:21.5.0'

    implementation("androidx.activity:activity-ktx:1.5.0")
    implementation("androidx.fragment:fragment-ktx:1.5.3")

    // Compose
    val composeBom = platform("androidx.compose:compose-bom:2024.05.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    implementation("androidx.compose.material3:material3")                       // UI
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.material:material-icons-extended")          // Icon
    implementation("androidx.compose.ui:ui-tooling-preview")                     // Preview support
    debugImplementation("androidx.compose.ui:ui-tooling")                        // Preview support
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")              // UI Tests
    debugImplementation("androidx.compose.ui:ui-test-manifest")                  // UI Tests

    // Jetpack Navigation
    val nav_version = "2.7.7"
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")
    implementation("androidx.navigation:navigation-compose:$nav_version")        // with compose
    implementation("androidx.compose.material:material:1.6.7")                  // Bottom Navigation
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")                // with Hilt

    implementation("com.google.accompanist:accompanist-pager:0.13.0")            // compose TabLayout
    implementation("com.google.accompanist:accompanist-pager-indicators:0.13.0") // compose TabLayout - Indicator
    implementation("com.google.accompanist:accompanist-appcompat-theme:0.28.0")

    // Room Database
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    testImplementation("androidx.room:room-testing:$room_version")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    kapt("com.github.bumptech.glide:compiler:4.12.0")

    // Retrofit2
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")                         // okhttp3
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")                            // Moshi
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")               // Moshi-Converter

    // Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")

    // Kotlin Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

    // Test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.5")
}