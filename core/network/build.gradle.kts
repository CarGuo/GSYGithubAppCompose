plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.apollo)
}

android {
    namespace = "com.shuyu.gsygithubappcompose.core.network"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

apollo {
    service("github") {
        packageName.set("com.shuyu.gsygithubappcompose.core.network.graphql")
        srcDir("src/main/graphql/github")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)

    implementation(projects.core.common)
    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Retrofit & OkHttp
    api(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.converter.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json)

    // Apollo
    api(libs.apollo.runtime)
    implementation(libs.apollo.api)
    implementation(libs.apollo.adapters)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
