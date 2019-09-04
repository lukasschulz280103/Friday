include(":app")

include("pocketsphinx-android-5prealpha-release")

include(":core")
project(":core").projectDir = File(rootDir, "appModules/core")

include(":core-ui")
project(":core-ui").projectDir = File(rootDir, "appModules/core-ui")

include(":ar")
project(":ar").projectDir = File(rootDir, "appModules/ar")

include(":wizard")
project(":wizard").projectDir = File(rootDir, "appModules/wizard")

include(":auth")
project(":auth").projectDir = File(rootDir, "appModules/auth")

include(":account")
project(":account").projectDir = File(rootDir, "appModules/account")

include(":store")
project(":store").projectDir = File(rootDir, "appModules/store")

include(":pluginSystem")
project(":pluginSystem").projectDir = File(rootDir, "appModules/pluginSystem")

include(":feedback")
project(":feedback").projectDir = File(rootDir, "appModules/feedback")