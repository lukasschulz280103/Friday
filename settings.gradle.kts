include(":app")

include("pocketsphinx-android-5prealpha-release")

include(":wizard")
project(":wizard").projectDir = File(rootDir, "appModules/wizard")

include(":auth")
project(":auth").projectDir = File(rootDir, "appModules/auth")