plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

android {
    namespace = "com.gabr.gabc.imguruploader"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.gabr.gabc.imguruploader"
        minSdk = 24
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
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }
    packaging {
        resources.excludes.add("META-INF/**")
    }
    kotlinOptions {
        jvmTarget = "18"
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
        unitTests.isIncludeAndroidResources = true
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {
    // Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.legacy:legacy-support-v13:1.0.0")
    implementation("androidx.fragment:fragment-ktx:1.6.1")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // LiveData and ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")

    // Dependency Injection
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")
    testImplementation("com.google.dagger:hilt-android-testing:2.48")
    kaptTest("com.google.dagger:hilt-android-compiler:2.48")

    // Robolectric
    testImplementation("org.robolectric:robolectric:4.10.3")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.2")

    // Coil for images
    implementation("io.coil-kt:coil:2.3.0")

    // Arrow
    implementation("io.arrow-kt:arrow-core:1.2.0")
    implementation("io.arrow-kt:arrow-fx-coroutines:1.2.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Mocks
    testImplementation("io.mockk:mockk:1.13.8")
}