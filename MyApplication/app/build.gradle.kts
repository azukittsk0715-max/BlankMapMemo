plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.gsmap"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.gsmap"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")

    // GPS
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // osmdroid
    implementation("org.osmdroid:osmdroid-android:6.1.18")
}
