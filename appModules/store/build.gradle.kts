apply(from = rootProject.file("base.gradle"))

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
}

dependencies {
    implementation(project(":core"))
    implementation(Dependencies.room)
    implementation(Dependencies.navigationFragment)
    implementation(Dependencies.navigationUi)
    implementation(Dependencies.navigationFragmentKtx)
    implementation(Dependencies.navigationUiKtx)
    implementation("com.github.bumptech.glide:glide:4.8.0")
    implementation("com.firebaseui:firebase-ui-storage:6.2.0")
    kapt(Dependencies.roomAnnotationProcessor)
    kapt("com.github.bumptech.glide:compiler:4.8.0")
    implementation(project(":pluginSystem"))
    implementation(Dependencies.zip4j)

}