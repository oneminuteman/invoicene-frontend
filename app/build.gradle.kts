plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.neinvoice"
    compileSdk = 34

    signingConfigs {
        create("release") {
            storeFile = file("C:\\Users\\Hp\\oneminuteman\\projects\\invoicene-backend\\myinvoiceapp\\config\\ssl\\localhost.crt")
            storePassword = "charachter" // Keystore password
            keyAlias = "mycert" // Alias for your key in the keystore
            keyPassword = "charachter" // Password for your key
        }
    }

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
            signingConfig = signingConfigs.getByName("release")
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
    implementation(libs.volley)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.recyclerview)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity.ktx)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso.core)
}
