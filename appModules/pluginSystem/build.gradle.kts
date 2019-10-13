apply(from = rootProject.file("base.gradle"))

plugins {
    id("com.android.library")
}

dependencies {
    implementation(project(":core"))
    implementation(Dependencies.room)
    implementation(Dependencies.zip4j)
}