import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
  id("war")
  alias(libs.plugins.spring.boot)
}

description = "Message API"

tasks.war {
  archiveFileName.set("MessageAPI.war")
}

dependencies {
  implementation(platform(SpringBootPlugin.BOM_COORDINATES))

  implementation(libs.bundles.spring)
  implementation(libs.spring.doc)

  testImplementation(libs.bundles.cucumber)
  testImplementation(libs.bundles.junit)
  testImplementation(libs.bundles.mockito)
  testImplementation(libs.bundles.spring.test)
}

sonar {
  properties {
    property("sonar.coverage.exclusions", "**/config/**, **/controller/**, **/errorhandling/**")
  }
}
