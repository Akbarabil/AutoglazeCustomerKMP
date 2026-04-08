import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    @Suppress("DEPRECATION")
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            implementation("io.ktor:ktor-client-cio:2.3.12")
            implementation(libs.play.services.location)
            implementation(libs.kotlinx.coroutines.play.services)
            implementation("io.insert-koin:koin-android:3.5.3")
            implementation("androidx.security:security-crypto:1.1.0-alpha06")
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.transitions)
            implementation(libs.voyager.screenmodel)
            implementation(libs.voyager.tab)
            implementation(libs.multiplatform.settings)
            // API & JSON (Gantinya Retrofit & Gson)
            implementation("io.ktor:ktor-client-core:2.3.12")

            implementation("io.ktor:ktor-client-content-negotiation:2.3.12")
            implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.12")
            implementation("io.ktor:ktor-client-logging:2.3.12")

            implementation("io.github.alexzhirkevich:qrose:1.0.1")
            // peekaboo-ui
            implementation("io.github.onseok:peekaboo-ui:0.5.2")
            implementation("io.github.onseok:peekaboo-image-picker:0.5.2")

            // Image Loading (Gantinya Glide)
            implementation("io.coil-kt.coil3:coil-compose:3.0.0-alpha08")

            // GANTI OkHttp dengan Ktor agar iOS bisa build
            implementation("io.coil-kt.coil3:coil-network-ktor:3.0.0-alpha08")
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")

            implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")

            // 1. Koin Core (Mesin Utama Koin)
            implementation("io.insert-koin:koin-core:3.5.3")

            // 2. Koin Compose (Agar Koin bisa jalan di UI Jetpack Compose)
            implementation("io.insert-koin:koin-compose:1.1.2")

            // 3. Voyager - Koin Integration (Ini yang bikin koinScreenModel() bisa jalan!)
            implementation("cafe.adriel.voyager:voyager-koin:1.0.0")

        }

        iosMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:2.3.12")
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.example.autoglazecustomer"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.autoglazecustomer"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }

        jniLibs {
            useLegacyPackaging = false
            @Suppress("UnstableApiUsage")
            doNotStrip += "**/libimage_processing_util_jni.so"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
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
    debugImplementation(libs.compose.uiTooling)
}

