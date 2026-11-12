plugins { id("com.android.application") }

android {
    namespace = "com.csu.census"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.csu.census"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.activity:activity:1.10.1")
    implementation("androidx.fragment:fragment:1.8.9")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.core:core:1.17.0")
    implementation("com.google.android.material:material:1.12.0")
}
