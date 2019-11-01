apply(from = rootProject.file("base.gradle"))
apply(plugin = "com.google.gms.google-services")
apply(plugin = "io.fabric")

plugins {
    id(Dependencies.Modules.androidApplicationPlugin)
}
android {
    defaultConfig {
        applicationId = "com.friday.ar"
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
}
dependencies {
    implementation(project(":core"))
    implementation(project(":core-ui"))
    implementation(project(":wizard"))
    implementation(project(":auth"))
    implementation(project(":account"))
    implementation(project(":ar"))
    implementation(project(":store"))
    implementation(project(":pluginSystem"))
    implementation(project(":feedback"))
    implementation(project(":preferences"))
    implementation(project(":dashboard"))
    implementation(project(Dependencies.pocketSphinx))


    implementation(Dependencies.androidPreference)
    implementation(Dependencies.constraintLayout)
    implementation(Dependencies.gridLayout)
    implementation(Dependencies.recyclerView)
    implementation(Dependencies.cardView)
    implementation(Dependencies.palette)
    implementation(Dependencies.crashlytics)
    implementation(Dependencies.firebaseStorage)
    implementation(Dependencies.firebaseDatabase)
    implementation(Dependencies.firebaseFirestore)
    implementation(Dependencies.dialogFlowAlpha) {
        exclude("com.google.api.grpc")
        exclude("com.google.protobuf")
    }
    implementation(Dependencies.firebaseMLVision)
    implementation(Dependencies.circularImageView)
    implementation(Dependencies.arCore)
    implementation(Dependencies.arSceneformCore)
    implementation(Dependencies.arSceneformUx)
    implementation(Dependencies.slidingUpPanel)
    implementation(Dependencies.zip4j)
    implementation(Dependencies.guava)
    testImplementation(Testing.koin)
    implementation(Dependencies.navigationFragment)
    implementation(Dependencies.navigationUi)
}

ant.importBuild("assets.xml")
//GoogleServicesPlugin.config.disableVersionCheck = true

