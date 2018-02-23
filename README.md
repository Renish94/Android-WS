# Android-WS
Android Web Services

# build.gradle (Project Level)
    allprojects {
        repositories {
            ...
            maven { url "https://jitpack.io" }
        }
    }

# build.gradle (Module Level)
    dependencies {
        implementation 'com.github.Renish94:Android-WS:v1.0'
    }
