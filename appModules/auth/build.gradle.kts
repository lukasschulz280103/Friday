apply(from = rootProject.file("base.gradle"))

plugins {
    id("com.android.library")
}

dependencies {
    implementation(Dependencies.crashlytics)
    implementation(project(":core"))
    implementation(project(":account"))
}
