import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.androidMultiplatformLibrary)
  alias(libs.plugins.composeMultiplatform)
  alias(libs.plugins.composeCompiler)
  alias(libs.plugins.kotlinSerialization)
  alias(libs.plugins.buildconfig)
}

kotlin {
  listOf(
    iosArm64(),
    iosSimulatorArm64()
  ).forEach { iosTarget ->
    iosTarget.binaries.framework {
      baseName = "Shared"
      isStatic = true
    }
  }

  android {
    namespace = "id.neo.hr.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    minSdk = libs.versions.android.minSdk.get().toInt()

    compilerOptions {
      jvmTarget = JvmTarget.JVM_11
    }
    androidResources {
      enable = true
    }
    withHostTest {
      isIncludeAndroidResources = true
    }
    withDeviceTestBuilder {
      sourceSetTreeName = "test"
    }.configure {
      instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
  }

  sourceSets {
    androidMain.dependencies {
      implementation(libs.compose.uiToolingPreview)
      implementation(libs.compose.uiTooling)
      implementation(libs.ktor.client.android)
    }
    commonMain.dependencies {
      implementation(project.dependencies.platform(libs.koin.bom))
      implementation(libs.compose.runtime)
      implementation(libs.compose.foundation)
      implementation(libs.compose.material3)
      implementation(libs.compose.ui)
      implementation(libs.compose.components.resources)
      implementation(libs.compose.uiToolingPreview)
      implementation(libs.androidx.lifecycle.viewmodelCompose)
      implementation(libs.androidx.lifecycle.runtimeCompose)
      implementation(libs.androidx.datastore.core)
      implementation(libs.androidx.datastore.preferences.core)
      implementation(libs.ktor.client.core)
      implementation(libs.ktor.client.contentNegotiation)
      implementation(libs.ktor.client.logging)
      implementation(libs.ktor.serialization.kotlinxJson)
      implementation(libs.kotlinx.datetime)
      implementation(libs.koin.compose)
      implementation(libs.koin.compose.viewmodel)
      implementation(libs.koin.core)
      implementation(libs.coil.compose)
      implementation("io.coil-kt.coil3:coil-network-ktor3:3.6.0")
      implementation("org.jetbrains.androidx.navigation:navigation-compose:2.10.0-alpha02")
      implementation(libs.material.icons.extended)
      implementation(libs.maplibre.compose)
      implementation(libs.maplibre.compose.material3)
      implementation(libs.napier)
      implementation(libs.camerak)
      implementation(libs.camerak.image.saver)
      implementation(libs.moko.permissions)
      implementation(libs.moko.permissions.compose)
      implementation(libs.moko.permissions.camera)
      implementation(libs.moko.permissions.location)
      implementation(libs.aliyun.oss)
    }
    androidMain.dependencies {
      runtimeOnly("org.maplibre.compose:maplibre-compose-runtime-vulkan-android:${libs.versions.maplibreCompose.get()}")
    }
    commonTest.dependencies {
      implementation(project.dependencies.platform(libs.koin.bom))
      implementation(libs.kotlin.test)
      implementation(libs.kotlinx.coroutinesTest)
      implementation(libs.ktor.client.mock)
      implementation(libs.koin.test)
    }
    appleMain.dependencies {
      implementation(libs.ktor.client.darwin)
    }
  }
}

dependencies {
  androidRuntimeClasspath(libs.compose.uiTooling)
}

val localProperties = Properties().apply {
  val localFile = rootProject.file("local.properties")
  if (localFile.exists()) {
    localFile.inputStream().use { load(it) }
  }
}

buildConfig {
  packageName("id.neo.hr")
  val baseUrl = (localProperties.getProperty("BASE_URL")
    ?: providers.gradleProperty("BASE_URL").getOrElse("")).trim('"')
  val ossEndpoint = (localProperties.getProperty("OSS_ENDPOINT")
    ?: providers.gradleProperty("OSS_ENDPOINT").getOrElse("")).trim('"')
  val ossBucketName = (localProperties.getProperty("OSS_BUCKET_NAME")
    ?: providers.gradleProperty("OSS_BUCKET_NAME").getOrElse("")).trim('"')
  val ossAccessKeyId = (localProperties.getProperty("OSS_ACCESS_KEY_ID")
    ?: providers.gradleProperty("OSS_ACCESS_KEY_ID").getOrElse("")).trim('"')
  val ossAccessKeySecret = (localProperties.getProperty("OSS_ACCESS_KEY_SECRET")
    ?: providers.gradleProperty("OSS_ACCESS_KEY_SECRET").getOrElse("")).trim('"')

  buildConfigField("BASE_URL", baseUrl)
  buildConfigField("OSS_ENDPOINT", ossEndpoint)
  buildConfigField("OSS_BUCKET_NAME", ossBucketName)
  buildConfigField("OSS_ACCESS_KEY_ID", ossAccessKeyId)
  buildConfigField("OSS_ACCESS_KEY_SECRET", ossAccessKeySecret)
}
