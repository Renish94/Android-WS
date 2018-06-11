# Android-WS
Android Web Services

## build.gradle (Project Level)
    allprojects {
        repositories {
            ...
            maven { url "https://jitpack.io" }
        }
    }

## build.gradle (Module Level)
    dependencies {
        implementation 'com.github.Renish94:Android-WS:v1.0'
    }

## Do not forget to add internet permission in manifest if already not present
    <uses-permission android:name="android.permission.INTERNET" />

### License
    Copyright (C) 2018 Renish Patel

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
