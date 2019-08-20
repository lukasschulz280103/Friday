include(":app")

include("pocketsphinx-android-5prealpha-release")

include(":core")
project(":core").projectDir = File(rootDir, "appModules/core")

include(":wizard")
project(":wizard").projectDir = File(rootDir, "appModules/wizard")

include(":auth")
project(":auth").projectDir = File(rootDir, "appModules/auth")

include(":account")
project(":account").projectDir = File(rootDir, "appModules/account")