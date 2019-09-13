# Module creation help
Follow these steps to create a new module for Friday

1. ## Create new module.
	Create new module by following these steps:
	- select `File`->`New`->`New Module`
	- select **Android Library**
	- Fill in the blanks and click *finish*
2. ## Move the module.
	Select the module folder of your newly created module and move it to `appModules`
3. ## Apply your changes to all gradle files.
	- *First, delete `settings.gradle` from the root project directory.*
	### settings.gradle.kts
		add the follwing lines to the settings.gradle.kts file:
		  include(":name")
		  project(":name").projectDir = File(rootDir, "appModules/name")
	### app/build.gradle.kts
		add the following dependency to `app/build.gradle.kts`:
		  implementation(project(":name"))
	### module/build.gradle
		rename to `build.gradle.kts`
		change the file conent to:
		
		  apply(from = rootProject.file("base.gradle"))
		  
		  plugins {
		  	id("com.android.library")
		  }
		  dependencies {
		  	implementation(project(":core"))
		  }
4. ## Synchronize project with gradle.
	 Click sync project with gradle files in the top right corner of android studio.
>  Now you can use your module.
