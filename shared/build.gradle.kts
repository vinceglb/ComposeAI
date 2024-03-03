import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.library)
    // kotlin("native.cocoapods")
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.buildKonfig)
    id("app.cash.sqldelight") version "2.0.1" // TODO alias(libs.plugins.sqlDelight)
    alias(libs.plugins.libres)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

//    iosX64()
//    iosArm64()
//    iosSimulatorArm64()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

//    cocoapods {
//        version = "1.0.0"
//        summary = "Some description for the Shared Module"
//        homepage = "Link to the Shared Module homepage"
//        ios.deploymentTarget = "14.1"
//        podfile = project.file("../iosApp/Podfile")
//        framework {
//            baseName = "shared"
//            isStatic = true
//        }
//        extraSpecAttributes["resources"] =
//            "['src/commonMain/resources/**', 'src/iosMain/resources/**']"
//
//        pod("FirebaseAnalytics") {
//            version = "~> 10.21"
//        }
//    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material) // https://github.com/adrielcafe/voyager/issues/185
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.materialIconsExtended)

            // Voyager
            implementation(libs.voyager.koin)
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.bottomSheetNavigator)

            // Koin
            implementation(libs.koin.core)

            // Resource
            // implementation(libs.resources)

            // OpenAI
            implementation(libs.openai.client)

            // Settings
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.coroutines)

            // SQL
            implementation(libs.coroutines.extensions)

            // UUID
            implementation(libs.uuid)

            // DateTime
            implementation(libs.kotlinx.datetime)

            // Napier (log)
            api(libs.napier)

            // Markdown
            implementation(libs.markdown)

            // Coil
            implementation(libs.coil3.compose)
            implementation(libs.coil3.svg)
            implementation(libs.coil3.network.ktor)
        }

        androidMain.dependencies {
            api(libs.activity.compose)
            api(libs.core.ktx)

            // Koin
            api(libs.koin.android)
            api(libs.koin.androidx.compose)

            // DataStore
            implementation(libs.datastore.preferences)

            // Settings
            implementation(libs.multiplatform.settings.datastore)

            // SQL
            implementation(libs.android.driver)

            // Accompanist
            implementation(libs.accompanist.systemuicontroller)

            // Splash Screen
            api(libs.core.splashscreen)

            // Firebase
            api(project.dependencies.platform(libs.firebase.bom))
            api(libs.firebase.analytics.ktx)
            api(libs.firebase.crashlytics.ktx)
            api(libs.firebase.appcheck.playintegrity)

            // AdMob
            api(libs.play.services.ads)

            // Billing
            implementation(libs.billing.ktx)

            // In-App Review
            implementation(libs.review.ktx)

            // Ktor
            implementation(libs.ktor.client.okhttp)
        }

        iosMain.dependencies {
            // SQL
            api(libs.native.driver)

            // Ktor
            implementation(libs.ktor.client.darwin)
        }
    }
}

compose {
    kotlinCompilerPlugin.set(libs.versions.kotlinComposeCompiler)
}

android {
    namespace = "com.ebfstudio.appgpt.common"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

//    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
//    sourceSets["main"].res.srcDirs("src/androidMain/res", "src/commonMain/resources")
//    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
    }

    buildFeatures {
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlinComposeCompiler.get()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }
}

buildkonfig {
    packageName = "com.ebfstudio.appgpt.common"

//    val props = Properties()
//
//    try {
//        props.load(file("../local.properties").inputStream())
//    } catch (e: Exception) {
//        // keys are private and can not be committed to git
//    }

    defaultConfigs {
        buildConfigField(
            STRING,
            "OPENAI_API_KEY",
            gradleLocalProperties(project.rootDir).getProperty("openai_api_key")
        )

        buildConfigField(
            STRING,
            "ADMOB_REWARDED_AD_ID",
            gradleLocalProperties(project.rootDir).getProperty("admob_rewarded_ad_id")
        )
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.ebfstudio.appgpt.common")
            schemaOutputDirectory.set(file("src/commonMain/sqldelight/databases"))
        }
    }
}

// https://github.com/Skeptick/libres
libres {
    generatedClassName = "MainRes"
    camelCaseNamesForAppleFramework = true
}

kotlin.sourceSets.all {
    languageSettings.optIn("com.russhwolf.settings.ExperimentalSettingsImplementation")
    languageSettings.optIn("com.russhwolf.settings.ExperimentalSettingsApi")
    languageSettings.optIn("com.aallam.openai.api.BetaOpenAI")
    languageSettings.optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
    languageSettings.optIn("androidx.compose.material3.ExperimentalMaterial3Api")
    languageSettings.optIn("androidx.compose.ui.ExperimentalComposeUiApi")
    languageSettings.optIn("androidx.compose.foundation.ExperimentalFoundationApi")
    languageSettings.optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
    languageSettings.optIn("kotlin.time.ExperimentalTime")
}