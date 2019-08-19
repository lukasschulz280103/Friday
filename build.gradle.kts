buildscript {
    repositories {
        maven("https://maven.fabric.io/public")
        google()
        jcenter()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.41")
        classpath("com.android.tools.build:gradle:3.5.0-rc03")
        classpath("com.google.gms:google-services:4.3.0")
        classpath("com.google.ar.sceneform:plugin:1.11.0")
        classpath("com.google.firebase:perf-plugin:1.3.1")
        classpath("io.fabric.tools:gradle:1.+")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.41")
    }


}

allprojects {
    repositories {
        google()
        jcenter()
        maven("https://jitpack.io")
    }
}

tasks.register("clean").configure {
    delete("build")
}