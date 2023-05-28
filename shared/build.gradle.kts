import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("com.codingfeline.buildkonfig")
    id("app.cash.sqldelight") version "2.0.0-alpha05"
    id("io.github.skeptick.libres")
}

kotlin {
    android()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        version = "1.0.0"
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
            isStatic = true
        }
        extraSpecAttributes["resources"] = "['src/commonMain/resources/**', 'src/iosMain/resources/**']"

        pod("FirebaseAnalytics") {
            version = "~> 10.9.0"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                implementation(compose.materialIconsExtended)

                // Voyager
                implementation(libs.voyager.navigator)

                // Koin
                implementation(libs.koin.core)

                // Image Loader
                implementation(libs.image.loader)

                // Resource
                implementation(libs.resources)

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

                // Libres (resources)
                implementation(libs.libres.compose)

                // Napier (log)
                api(libs.napier)

                // Markdown
                implementation(libs.markdown)
            }
        }
        val androidMain by getting {
            dependencies {
                api(libs.activity.compose)
                api(libs.appcompat)
                api(libs.core.ktx)

                // Koin
                api(libs.koin.android)

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
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)

            dependencies {
                // SQL
                api(libs.native.driver)
            }
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "com.ebfstudio.appgpt.common"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res", "src/commonMain/resources")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        jvmToolchain(11)
    }
}

buildkonfig {
    packageName = "com.ebfstudio.appgpt.common"

    val props = Properties()

    try {
        props.load(file("../local.properties").inputStream())
    } catch (e: Exception) {
        // keys are private and can not be committed to git
    }

    defaultConfigs {
        buildConfigField(
            STRING,
            "OPENAI_API_KEY",
            props["openai_api_key"]?.toString() ?: "abc"
        )

        buildConfigField(
            STRING,
            "ADMOB_REWARDED_AD_ID",
            props["admob_rewarded_ad_id"]?.toString() ?: "abc"
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