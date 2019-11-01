apply(from = rootProject.file("base.gradle"))

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
}

dependencies {
    implementation(project(":core"))
    implementation(Dependencies.room)
    kapt(Dependencies.roomAnnotationProcessor)
    implementation(project(":pluginSystem"))
    implementation(Dependencies.zip4j)

}