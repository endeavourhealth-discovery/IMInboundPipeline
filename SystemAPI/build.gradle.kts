import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
  id("war")
  alias(libs.plugins.spring.boot)
}

description = "System API"

tasks.war {
  archiveFileName.set("SystemAPI.war")
}

dependencies {
  implementation(platform(SpringBootPlugin.BOM_COORDINATES))

  implementation(project(":CommonAPI"))

  implementation(libs.bundles.spring)
  implementation(libs.spring.doc)
  implementation(libs.mysql)
}

sonar {
  properties {
    property("sonar.coverage.exclusions", "**/config/**, **/controller/**, **/errorhandling/**")
  }
}
