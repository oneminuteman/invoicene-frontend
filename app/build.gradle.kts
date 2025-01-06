plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.neinvoice"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.neinvoice"
        minSdk = 29
        targetSdk = 34
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
}

dependencies {

    dependencies {
        implementation(libs.retrofit)
        implementation(libs.converter.gson) // Fix the gson reference
        implementation(libs.recyclerview)
        implementation(libs.appcompat)
        implementation(libs.material)
        implementation(libs.activity.ktx) // Fix activity-ktx reference
        implementation(libs.constraintlayout)
        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit) // Fix androidx-junit reference
        androidTestImplementation(libs.espresso.core) // Fix espresso-core reference
    }

}