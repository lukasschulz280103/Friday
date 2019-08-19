apply(rootProject.file("base.gradle.kts"))
apply(plugin = "com.google.gms.google-services")

plugins {
    id("com.android.application")
    id("com.google.ar.sceneform.plugin")
}
android {
    compileSdkVersion(29)
    defaultConfig {
        applicationId = "com.friday.ar"
        minSdkVersion(26)
        targetSdkVersion(29)
        versionCode = 10
        versionName = "1.3.1-alpha01"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    packagingOptions {
        exclude("META-INF/*.kotlin_module")
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/LICENSE")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/license.txt")
        exclude("META-INF/NOTICE")
        exclude("META-INF/NOTICE.txt")
        exclude("META-INF/notice.txt")
        exclude("META-INF/ASL2.0")
        exclude("META-INF/INDEX.LIST")
    }
    useLibrary("android.test.base")
    useLibrary("android.test.mock")
    dataBinding {
        isEnabled = true
    }
}
dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.aar", "*.jar"), "dir" to "libs")))
    implementation("androidx.preference:preference:1.1.0-rc01")
    implementation("androidx.appcompat:appcompat:1.1.0-rc01")
    implementation("android.arch.lifecycle:extensions:1.1.1")
    implementation("android.arch.lifecycle:viewmodel:1.1.1")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("androidx.gridlayout:gridlayout:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.1.0-beta02")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.palette:palette-ktx:1.0.0")
    implementation("androidx.lifecycle:lifecycle-runtime:2.2.0-alpha03")
    implementation("com.google.android.material:material:1.1.0-alpha09")
    implementation("com.google.firebase:firebase-core:17.1.0")
    implementation("com.google.firebase:firebase-database:19.0.0")
    implementation("com.google.firebase:firebase-storage:19.0.0")
    implementation("com.google.firebase:firebase-auth:19.0.0")
    implementation("com.google.firebase:firebase-perf:19.0.0")
    implementation("com.google.firebase:firebase-firestore:21.0.0")
    implementation("com.google.firebase:firebase-ml-vision:23.0.0")
    implementation("com.google.android.gms:play-services-auth:17.0.0")
    implementation("com.google.cloud:google-cloud-dialogflow:0.103.0-alpha") {
        exclude("com.google.api.grpc")
        exclude("com.google.protobuf")
    }
    implementation("com.mikhaellopez:circularimageview:3.2.0")
    implementation("com.google.ar.sceneform.ux:sceneform-ux:1.11.0")
    implementation("com.google.ar.sceneform:core:1.11.0")
    implementation("com.google.ar:core:1.11.0")
    implementation("com.github.AppIntro:AppIntro:v5.1.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("com.crashlytics.sdk.android:crashlytics:2.10.1")
    implementation("com.sothree.slidinguppanel:library:3.4.0")
    implementation("net.lingala.zip4j:zip4j:1.3.3")
    implementation("com.google.guava:guava:28.0-jre")
    implementation("org.koin:koin-core:2.0.1")
    implementation("org.koin:koin-android:2.0.1")
    testImplementation("junit:junit:4.12")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.3.41")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.41")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.0-RC")
    implementation("androidx.navigation:navigation-fragment-ktx:2.0.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.0.0")
}

ant.importBuild("assets.xml")
//GoogleServicesPlugin.config.disableVersionCheck = true
sceneform.asset("sampledata/models/project_friday_text.fbx",
        "default",
        "sampledata/models/project_friday_text.sfa",
        "src/main/assets/project_friday_text")
