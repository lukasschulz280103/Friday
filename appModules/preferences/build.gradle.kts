apply(from = rootProject.file("base.gradle"))

plugins {
    id("com.android.library")
}
dependencies {
    implementation(project(":core"))
    implementation(project(":core-ui"))
    implementation(project(":account"))
}