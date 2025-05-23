import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
  id("java-library")
  alias(libs.plugins.spring.boot) apply false
}

description = "Common API"

tasks.jar {
  archiveFileName.set("CommonAPI.jar")
}

dependencies {
  implementation(platform(SpringBootPlugin.BOM_COORDINATES))

  implementation(libs.bundles.spring)

  testImplementation(libs.bundles.cucumber)
  testImplementation(libs.bundles.junit)
  testImplementation(libs.bundles.mockito)
  testImplementation(libs.bundles.spring.test)
}
