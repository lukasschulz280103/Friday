apply(from = rootProject.file("base.gradle"))

plugins {
    id("com.android.library")
    id(Dependencies.Modules.sceneFromPLugin)
}
dependencies {
    implementation(project(":core"))
    implementation(Dependencies.arCore)
    implementation(Dependencies.arSceneformCore)
    implementation(Dependencies.arSceneformUx)
}

sceneform.asset("src/main/assets/models/project_friday_text.fbx",
        "default",
        "src/main/assets/models/project_friday_text.sfa",
        "src/main/assets/project_friday_text")