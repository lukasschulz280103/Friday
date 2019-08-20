apply(from = rootProject.file("base.gradle"))

plugins {
    id("com.android.library")
}
dependencies {
    implementation(project(":auth"))
    implementation(project(":core"))
    implementation(Dependencies.androidPreference)
    implementation(Dependencies.appIntro)
    implementation(Dependencies.arCore)
}
