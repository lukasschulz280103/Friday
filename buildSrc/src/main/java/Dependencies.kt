private object Versions {
    const val buildToolsVersion = "3.5.0"
    const val kotlinVersion = "1.3.70"
    const val kotlinCoroutines = "1.3.5"
    const val arCoreVersion = "1.16.0"
    const val sceneformVersion = "1.15.0"

    const val zip4j = "1.3.3"

    const val jetpack = "1.1.0"
    const val constraintLayout = "1.1.3"
    const val gridLayout = "1.0.0"
    const val ktx = "1.2.0"
    const val guava = "28.2-jre"
    const val palette = "1.0.0"
    const val legacy = "1.0.0"

    const val play = "17.0.0"

    const val androidArch = "2.1.0"

    const val navigation = "2.2.1"
    const val room = "2.2.5"
    const val recyclerView = "1.0.0"
    const val material = "1.0.0"
    const val materialAlpha = "1.2.0-alpha05"
    const val CIV = "4.2.0"
    const val SUP = "3.4.0"
    const val appIntro = "5.1.0"

    const val firebaseCore = "17.2.0"
    const val firebaseFeatures = "19.1.0"
    const val firebasePerformance = "19.0.5"
    const val firebaseFirestore = "21.4.1"
    const val firebaseMl = "24.0.1"
    const val crashlytics = "2.10.1"

    const val gcloudDialogFlow = "1.0.0"

    const val koin = "2.1.5"

    // Testing
    const val junitGradle = "1.4.2.1"
    const val junit = "5.6.0"
    const val mockitoKotlin = "2.2.0"
    const val kotlinTest = "3.4.2"

    const val glide = "4.11.0"
}

object BuildPlugins {

    const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.buildToolsVersion}"
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinVersion}"
    const val arCoreSceneformPlugin = "com.google.ar.sceneform:plugin:1.15.0"
    const val gmsServices = "com.google.gms:google-services:4.3.3"

    const val kotlinAndroid = "kotlin-android"
    const val kotlinAndroidExtensions = "kotlin-android-extensions"
    const val kotlinKapt = "kotlin-kapt"

}

object Dependencies {

    object Modules {
        const val androidApplicationPlugin = "com.android.application"
        const val androidLibraryPlugin = "com.android.library"
        const val sceneFromPLugin = "com.google.ar.sceneform.plugin"
    }

    const val koinAndroid = "org.koin:koin-android:${Versions.koin}"
    const val koinAndroidScope = "org.koin:koin-android-scope:${Versions.koin}"
    const val koinAndroidViewModel = "org.koin:koin-android-viewmodel:${Versions.koin}"

    //kotlin&appCompat
    const val androidPreference = "androidx.preference:preference:${Versions.jetpack}"
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlinVersion}"
    const val appCompat = "androidx.appcompat:appcompat:${Versions.jetpack}"
    const val ktxCore = "androidx.core:core-ktx:${Versions.ktx}"
    const val guava = "com.google.guava:guava:${Versions.guava}"
    const val kotlinCoroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.kotlinCoroutines}"
    const val palette = "androidx.palette:palette-ktx:${Versions.palette}"
    const val legacy = "androidx.legacy:legacy-support-v4:${Versions.legacy}"
    const val room = "androidx.room:room-runtime:${Versions.room}"
    const val roomAnnotationProcessor = "android.arch.persistence.room:compiler:${Versions.room}"
    //fileManagement
    const val zip4j = "net.lingala.zip4j:zip4j:${Versions.zip4j}"

    //play
    const val playServices = "com.google.android.gms:play-services-auth:${Versions.play}"

    //layoutlibs
    const val material = "com.google.android.material:material:${Versions.materialAlpha}"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    const val gridLayout = "androidx.gridlayout:gridlayout:${Versions.gridLayout}"
    const val recyclerView = "androidx.recyclerview:recyclerview:${Versions.recyclerView}"
    const val cardView = "androidx.cardview:cardview:${Versions.material}"
    const val circularImageView = "com.mikhaellopez:circularimageview:${Versions.CIV}"
    const val slidingUpPanel = "com.sothree.slidinguppanel:library:${Versions.SUP}"
    const val appIntro = "com.github.AppIntro:AppIntro:${Versions.appIntro}"

    //android arch
    const val androidArchLifecycleViewModel = "android.arch.lifecycle:extensions:${Versions.androidArch}"
    const val androidArchLifecyleExtensions = "android.arch.lifecycle:viewmodel:${Versions.androidArch}"
    const val androidArchLifeCycleRuntime = "androidx.lifecycle:lifecycle-runtime:${Versions.androidArch}"
    const val andoridArchLifecycleKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.androidArch}"

    //navigation
    const val navigationFragment = "androidx.navigation:navigation-fragment:${Versions.navigation}"
    const val navigationUi = "androidx.navigation:navigation-ui:${Versions.navigation}"
    const val navigationFragmentKtx = "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"
    const val navigationUiKtx = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"

    // AR
    const val arCore = "com.google.ar:core:${Versions.arCoreVersion}"
    const val arSceneformUx = "com.google.ar.sceneform.ux:sceneform-ux:${Versions.sceneformVersion}"
    const val arSceneformCore = "com.google.ar.sceneform:core:${Versions.sceneformVersion}"

    //firebase
    const val firebaseCore = "com.google.firebase:firebase-core:${Versions.firebaseCore}"
    const val firebaseDatabase = "com.google.firebase:firebase-database:${Versions.firebaseFeatures}"
    const val firebaseStorage = "com.google.firebase:firebase-storage:${Versions.firebaseFeatures}"
    const val firebaseAuth = "com.google.firebase:firebase-auth:${Versions.firebaseFeatures}"
    const val firebasePerformance = "com.google.firebase:firebase-perf:${Versions.firebasePerformance}"
    const val firebaseFirestore = "com.google.firebase:firebase-firestore:${Versions.firebaseFirestore}"
    const val firebaseMLVision = "com.google.firebase:firebase-ml-vision:${Versions.firebaseMl}"
    const val crashlytics = "com.crashlytics.sdk.android:crashlytics:${Versions.crashlytics}"
    const val firebaseUiStorage = "com.firebaseui:firebase-ui-storage:6.2.0"

    //speech assistant
    const val dialogFlowAlpha = "com.google.cloud:google-cloud-dialogflow:${Versions.gcloudDialogFlow}"
    const val pocketSphinx = ":pocketsphinx-android-5prealpha-release"

    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    const val glideCompiler = "com.github.bumptech.glide:compiler:${Versions.glide}"
}

object Testing {
    // Junit
    const val junitJupiterApi = "org.junit.jupiter:junit-jupiter-api:${Versions.junit}"
    const val junitJupiterEngine = "org.junit.jupiter:junit-jupiter-engine:${Versions.junit}"
    const val mockitoKotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockitoKotlin}"

    // Kotlin
    const val kotlinTestAssertions = "io.kotlintest:kotlintest-assertions:${Versions.kotlinTest}"
    const val kotlinTestJunitRunner = "io.kotlintest:kotlintest-runner-junit5:${Versions.kotlinTest}"
    const val kotlinTestKoinExtensions = "io.kotlintest:kotlintest-extensions-koin:${Versions.kotlinTest}"
    const val kotlinCoroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.kotlinCoroutines}"

    // Android X
    const val room = "androidx.room:room-testing:${Versions.room}"
    const val lifecycle = "androidx.arch.core:core-testing:${Versions.androidArch}"

    // Dependency Injection
    const val koin = "org.koin:koin-test:${Versions.koin}"
}