import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.buildKonfig)
    alias(libs.plugins.sqlDelight)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

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

            // RevenueCat
            api(libs.purchases.core)
            implementation( libs.purchases.ui)
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

            // Fix SQDelight bug
            implementation(libs.stately.common)
        }

        // RevenueCat setup
        // https://www.revenuecat.com/docs/getting-started/installation/kotlin-multiplatform#opt-in-to-experimentalforeignapi
        named { it.lowercase().startsWith("ios") }.configureEach {
            languageSettings {
                optIn("kotlinx.cinterop.ExperimentalForeignApi")
            }
        }
    }

    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xexpect-actual-classes",
            "-Xopt-in=kotlin.time.ExperimentalTime",
        )
    }
}

android {
    namespace = "com.ebfstudio.appgpt.common"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
    }

    buildFeatures {
        buildConfig = true
    }

//    composeOptions {
//        kotlinCompilerExtensionVersion = libs.versions.kotlinComposeCompiler.get()
//    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

//    kotlin {
//        jvmToolchain(17)
//    }
}

buildkonfig {
    packageName = "com.ebfstudio.appgpt.common"
    exposeObjectWithName = "BuildKonfigCommon"

    defaultConfigs {
        buildConfigField(
            STRING,
            "OPENAI_API_KEY",
            gradleLocalProperties(project.rootDir, providers).getProperty("openai_api_key")
        )

        buildConfigField(
            STRING,
            "ADMOB_REWARDED_AD_ID",
            gradleLocalProperties(project.rootDir, providers).getProperty("admob_rewarded_ad_id")
        )

        buildConfigField(
            STRING,
            "REVENUECAT_API_KEY",
            gradleLocalProperties(project.rootDir, providers).getProperty("revenuecat_api_key")
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