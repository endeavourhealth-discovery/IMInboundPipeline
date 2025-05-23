import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
  alias(libs.plugins.spring.boot) apply false
}

description = "Transform"

tasks.jar {
  archiveFileName.set("Transform.jar")
}

dependencies {
  implementation(platform(SpringBootPlugin.BOM_COORDINATES))

  implementation(libs.jackson)
  implementation(libs.jslt)
  implementation(libs.slf4j)

  testImplementation(libs.bundles.cucumber)
  testImplementation(libs.bundles.junit)
  testImplementation(libs.bundles.mockito)
  testImplementation(libs.bundles.spring.test)
}
