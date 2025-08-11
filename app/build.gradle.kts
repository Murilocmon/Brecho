plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.brecho"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.brecho"
        minSdk = 24
        targetSdk = 35
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    viewBinding{
        enable = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("androidx.room:room-runtime:2.4.0")
    annotationProcessor ("androidx.room:room-compiler:2.4.0")
    implementation ("androidx.databinding:viewbinding:7.1.2")
    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    implementation ("com.google.android.material:material:1.5.0")
    implementation ("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation ("androidx.databinding:viewbinding:7.1.2")
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.android.volley:volley:1.2.1")
}