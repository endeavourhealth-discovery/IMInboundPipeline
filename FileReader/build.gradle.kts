import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
  alias(libs.plugins.spring.boot)
}

description = "File Reader"

tasks.bootJar {
  mainClass.set("org.endeavourhealth.im.FilePoller")
  archiveFileName.set("FileReader.jar")
}

dependencies {
  implementation(platform(SpringBootPlugin.BOM_COORDINATES))

  implementation(libs.apache.csv)
  implementation(libs.aws.s3)
  implementation(libs.spring.amqp)
  implementation(libs.spring.web)

  testImplementation(libs.bundles.cucumber)
  testImplementation(libs.bundles.junit)
  testImplementation(libs.bundles.mockito)
  testImplementation(libs.bundles.spring.test)
}

sonar {
  properties {
    property("sonar.coverage.exclusions", "**/config/**, **/controller/**, **/model,**")
  }
}
