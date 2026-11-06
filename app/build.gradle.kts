plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}
android {
    namespace = "com.brendigo.lensigo"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.brendigo.lensigo"
        minSdk = 24
        targetSdk = 36
        versionCode = 6
        versionName = "0.6.0"
    }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    buildFeatures { compose = true; buildConfig = true }
}
dependencies {
    implementation(platform("androidx.compose:compose-bom:2025.12.00"))
    implementation("androidx.activity:activity-compose:1.12.0")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.4")
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.exifinterface:exifinterface:1.4.1")
    implementation("androidx.annotation:annotation-experimental:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    implementation("androidx.camera:camera-core:1.6.2")
    implementation("androidx.camera:camera-camera2:1.6.2")
    implementation("androidx.camera:camera-lifecycle:1.6.2")
    implementation("androidx.camera:camera-view:1.6.2")
    implementation("androidx.camera:camera-video:1.6.2")
    implementation("androidx.camera:camera-extensions:1.6.2")
    testImplementation("junit:junit:4.13.2")
}

/**
 * Generate the reference-identical launcher asset before Android resource merging.
 * Python 3 + Pillow 12.3, numpy 2.3.5, CairoSVG 2.8.2 are required on build hosts.
 */
val generateLensigoLauncher by tasks.registering(Exec::class) {
    group = "branding"
    description = "Regenerates the approved 512px launch icon"
    workingDir = rootProject.projectDir
    commandLine("python", "tools/generate_launcher.py",
        "app/src/main/res/drawable-nodpi/lensigo_launcher.png")
    inputs.file(rootProject.file("tools/generate_launcher.py"))
    outputs.file(file("src/main/res/drawable-nodpi/lensigo_launcher.png"))
}
tasks.named("preBuild").configure { dependsOn(generateLensigoLauncher) }
