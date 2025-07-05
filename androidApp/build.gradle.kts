plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.compose.compiler)
    id("com.google.firebase.crashlytics")
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.ebfstudio.appgpt"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.ebfstudio.appgpt"
        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()
        versionCode = 29
        versionName = "1.6.1"
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        // isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    // See more
    // https://developer.android.com/build/shrink-code#kts
    buildTypes {
        getByName("release") {
            // Enables code shrinking, obfuscation, and optimization for only
            // your project's release build type. Make sure to use a build
            // variant with `isDebuggable=false`.
            isMinifyEnabled = true

            // Enables resource shrinking, which is performed by the
            // Android Gradle plugin.
            isShrinkResources = true

            // Includes the default ProGuard rules files that are packaged with
            // the Android Gradle plugin. To learn more, go to the section about
            // R8 configuration files.
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(project(":shared"))

    // Android
    implementation(libs.activity.compose)
    implementation(libs.core.ktx)

    // Java 8+ API desugaring support
    // - A subset of java.time
    // - https://developer.android.com/studio/write/java8-support#library-desugaring
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}
