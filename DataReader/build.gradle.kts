import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
  alias(libs.plugins.spring.boot)
}

description = "Data Reader"

tasks.bootJar {
  mainClass.set("org.endeavourhealth.im.DataPoller")
  archiveFileName.set("DataReader.jar")
}

dependencies {
  implementation(platform(SpringBootPlugin.BOM_COORDINATES))

  implementation(project(":Transform"))
  implementation(libs.spring.amqp)
  implementation(libs.spring.web)

  testImplementation(libs.bundles.junit)
  testImplementation(libs.bundles.spring.test)
}
