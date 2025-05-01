import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose.compiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.google.dagger.hilt.android)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.firebase.crashlytics)
}

val localProperties = Properties()
localProperties.load(FileInputStream(rootProject.file("local.properties")))

android {
    namespace = "com.w36495.senty"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.w36495.senty"
        minSdk = 24
        targetSdk = 34
        versionCode = 16
        versionName = "2.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "FIREBASE_WEB_API", localProperties.getProperty("FIREBASE_WEB_API"))
        buildConfigField("String", "FIREBASE_DATABASE_BASE_URL", localProperties.getProperty("FIREBASE_DATABASE_BASE_URL"))
        buildConfigField("String", "NAVER_GEOCODING_BASE_URI", localProperties.getProperty("NAVER_GEOCODING_BASE_URI"))
        buildConfigField("String", "NAVER_MAP_KEY", localProperties.getProperty("NAVER_MAP_KEY"))
        buildConfigField("String", "NAVER_MAP_SECRET_KEY", localProperties.getProperty("NAVER_MAP_SECRET_KEY"))
        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", localProperties.getProperty("KAKAO_NATIVE_APP_KEY"))
        buildConfigField("String", "GOOGLE_CLOUD_WEB_CLIENT_ID", localProperties.getProperty("GOOGLE_CLOUD_WEB_CLIENT_ID"))
        buildConfigField("String", "SUGGESTION_BOX_URL", localProperties.getProperty("SUGGESTION_BOX_URL"))
        manifestPlaceholders["NAVER_MAP_KEY"] = localProperties.getProperty("NAVER_MAP_KEY")
        manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = localProperties.getProperty("KAKAO_NATIVE_APP_KEY")
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
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.bundles.lifecycle)

    // Compose
    implementation(libs.bundles.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Room, DataStore
    implementation(libs.bundles.database)
    annotationProcessor(libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler)

    // Network (Retrofit, Okhttp, Serialization)
    implementation(libs.bundles.retrofit)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    // Login (Google, Kakao)
    implementation(libs.bundles.login)

    // Map (naver, gms location)
    implementation(libs.bundles.naverMap)

    // Hilt
    implementation(libs.bundles.hilt)
    ksp(libs.hilt.compiler)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Other
    implementation(libs.thired.party.date.picker.compose)
    implementation(libs.thired.party.three.ten.abp)
    implementation(libs.thired.party.grid.layout.compose)

    // Test
    testImplementation(libs.bundles.test)
    androidTestImplementation(libs.bundles.androidTest)
}