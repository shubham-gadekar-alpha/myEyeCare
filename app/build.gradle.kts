plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.service)
    alias(libs.plugins.hilt.android)
    id("kotlin-kapt")
    id("jacoco")
    id("io.gitlab.arturbosch.detekt")
    id("org.jlleitschuh.gradle.ktlint")
}

android {
    namespace = "com.alpha.myeyecare"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.alpha.myeyecare"
        minSdk = 24
        targetSdk = 35
        versionCode = 2
        versionName = "1.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
        }

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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

jacoco {
    toolVersion = "0.8.10" // use latest
}

tasks.withType<Test>().configureEach {
    extensions.configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}


tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest") // make sure tests run first

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(true)
    }

    val debugTree = fileTree("${project.buildDir}/tmp/kotlin-classes/debug") {
        include("**/com/alpha/**")
        exclude(
            "**/R.class",
            "**/R$*.class",
            "**/BuildConfig.*",
            "**/Manifest*.*",
            "**/*Test*.*",
            "android/**/*.*",

            "**/com/alpha/myeyecare/presentation/ui/detailScreen/SetupReminderScreen*.*",
            "**/presentation/ui/detailScreen/ComposableSingletons\$SetupReminderScreen*.*",

            "**/com/alpha/myeyecare/presentation/ui/suggestion/UserSuggestionScreen*.*",
            "**/presentation/ui/suggestion/ComposableSingletons\$UserSuggestionScreen*.*",

            "**/com/alpha/myeyecare/presentation/ui/splash/SplashScreen*.*",
            "**/presentation/ui/splash/ComposableSingletons\$SplashScreen*.*",

            "**/com/alpha/myeyecare/presentation/ui/CheckUserNotificationPermission*.*",
            "**/presentation/ui/ComposableSingletons\$CheckUserNotificationPermission*.*",

            "**/com/alpha/myeyecare/presentation/ui/home/HomeScreen*.*",
            "**/presentation/ui/home/ComposableSingletons\$HomeScreen*.*",

            "**/com/alpha/myeyecare/presentation/ui/common/**/*.*",
            "**/com/alpha/myeyecare/common/**/*.*",
            "**/com/alpha/myeyecare/data/**/*.*",
            "**/com/alpha/myeyecare/di/**/*.*",
            "**/com/alpha/myeyecare/domain/model/**/*.*",
            "**/com/alpha/myeyecare/domain/repository/**/*.*",
            "**/com/alpha/myeyecare/presentation/navigation/**/*.*",
            "**/com/alpha/myeyecare/presentation/ui/theme/**/*.*",
            "**/com/alpha/myeyecare/worker/**/*.*",

            "**/MyApplication*.*",
            "**/MainActivity*.*",
            "**/ComposableSingletons\$MainActivity*.*",
        )
    }

    sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree(project.buildDir) {
        include(
            "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec"
        )
    })
}

detekt {
    toolVersion = "1.23.6"
    config.setFrom("$rootDir/config/detekt/detekt.yml") // optional custom rules
    buildUponDefaultConfig = true
    reports {
        html.required.set(true)
        txt.required.set(true)
        xml.required.set(true)
    }
}

ktlint {
    version.set("1.2.1")
    android.set(true)
    outputToConsole.set(true)
    ignoreFailures.set(false)
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)

    // Work Manager
    implementation(libs.androidx.work.runtime.ktx)

    // Junit
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.analytics)

    // Compose Nav
    implementation(libs.androidx.navigation.compose)

    // Material Lib
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended)

    // Gson
    implementation(libs.gson)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // ViewModel & Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    // Notification Permission
    implementation(libs.accompanist.permissions)

    // Testing
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.turbine)
    testImplementation(libs.truth)

    // Mocking
    testImplementation(libs.mockk)

    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    testImplementation(libs.coroutines.test)

    testImplementation(libs.androidx.core.testing)

    detektPlugins(libs.detekt.formatting)
}

kapt {
    correctErrorTypes = true
}
