plugins {
    id("com.android.library")
}
apply(from = rootProject.file("base.gradle"))

dependencies {
    implementation("org.koin:koin-core:2.0.1")
// Koin extended & experimental features
    implementation("org.koin:koin-core-ext:2.0.1")
// Koin for Unit tests
    testImplementation("org.koin:koin-test:2.0.1")
// Koin for Java developers

    implementation("androidx.preference:preference:1.1.0-rc01")
    implementation("org.koin:koin-java:2.0.1")
    implementation("com.github.AppIntro:AppIntro:v5.1.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.41")
    implementation("androidx.appcompat:appcompat:1.1.0-rc01")
    implementation("androidx.core:core-ktx:1.0.2")
    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test.ext:junit:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
}
