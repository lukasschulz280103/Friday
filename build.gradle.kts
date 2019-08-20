buildscript {
    extensions.add("enableCrashlytics", true)
    repositories {
        maven("https://maven.fabric.io/public")
        google()
        jcenter()
    }
    dependencies {
        classpath(BuildPlugins.kotlinGradlePlugin)
        classpath(BuildPlugins.androidGradlePlugin)
        classpath(BuildPlugins.gmsServices)
        classpath(BuildPlugins.arCoreSceneformPlugin)
        classpath("com.google.firebase:perf-plugin:1.3.1")
        classpath("io.fabric.tools:gradle:1.+")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
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