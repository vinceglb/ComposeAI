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
    }

    val settingsVersion = "1.0.0"
    val koinVersion = "3.4.0"
    val sqlDelight = "2.0.0-alpha05"

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
                val voyagerVersion = "1.0.0-rc05"
                implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")

                // Koin
                api("io.insert-koin:koin-core:$koinVersion")

                // Image Loader
                val imageLoaderVersion = "1.4.0"
                implementation("io.github.qdsfdhvh:image-loader:$imageLoaderVersion")

                // Resource
                implementation("com.goncalossilva:resources:0.3.2")

                // OpenAI
                implementation("com.aallam.openai:openai-client:3.2.3")

                // Settings
                implementation("com.russhwolf:multiplatform-settings:$settingsVersion")
                implementation("com.russhwolf:multiplatform-settings-coroutines:$settingsVersion")

                // SQL
                implementation("app.cash.sqldelight:coroutines-extensions:$sqlDelight")

                // UUID
                implementation("com.benasher44:uuid:0.7.0")

                // DateTime
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

                // Libres (resources)
                implementation("io.github.skeptick.libres:libres-compose:1.1.8")
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.activity:activity-compose:1.7.1")
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.10.0")

                // Koin
                api("io.insert-koin:koin-android:$koinVersion")

                // DataStore
                implementation("androidx.datastore:datastore-preferences:1.0.0")

                // Settings
                implementation("com.russhwolf:multiplatform-settings-datastore:$settingsVersion")

                // SQL
                implementation("app.cash.sqldelight:android-driver:$sqlDelight")

                // Accompanist
                implementation("com.google.accompanist:accompanist-systemuicontroller:0.30.1")
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
                api("app.cash.sqldelight:native-driver:$sqlDelight")
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
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.ebfstudio.appgpt.common")
        }
    }
}

// https://github.com/Skeptick/libres
libres {
    generatedClassName = "MainRes"
    camelCaseNamesForAppleFramework = true
}

kotlin.sourceSets.all {
    languageSettings.optIn("com.russhwolf.settings.ExperimentalSettingsApi")
    languageSettings.optIn("com.aallam.openai.api.BetaOpenAI")
    languageSettings.optIn("com.russhwolf.settings.ExperimentalSettingsImplementation")
    languageSettings.optIn("androidx.compose.material3.ExperimentalMaterial3Api")
    languageSettings.optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
    languageSettings.optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
    languageSettings.optIn("androidx.compose.ui.ExperimentalComposeUiApi")
}