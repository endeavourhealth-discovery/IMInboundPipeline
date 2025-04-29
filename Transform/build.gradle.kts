import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
  id("java")
  id("jacoco")
  alias(libs.plugins.sonar)
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

if (System.getenv("ENV") == "prod") {
  tasks.build {
    finalizedBy("sonar")
  }
}

sonar.properties {
  property("sonar.token", System.getenv("SONAR_LOGIN"))
  property("sonar.gradle.skipCompile", true)
  property("sonar.organization", "endeavourhealth-discovery")
  property("sonar.projectKey", "IMInboundPipeline_Transform")
  property("sonar.projectName", "Transform")
  property("sonar.host.url", "https://sonarcloud.io")
  property("sonar.junit.reportPaths", "build/test-results/test")
}

tasks.test {
  jvmArgs("-XX:+EnableDynamicAgentLoading")
  useJUnitPlatform()
  finalizedBy("jacocoTestReport")
}

tasks.jacocoTestReport {
  reports {
    xml.required.set(true)
  }
}

val cucumberRuntime by getConfigurations().creating {
  extendsFrom(configurations["testImplementation"])
}

tasks.register("cucumberCli") {
  dependsOn("assemble", "testClasses")
  doLast {
    providers.javaexec {
      mainClass.set("io.cucumber.core.cli.Main")
      classpath = cucumberRuntime + sourceSets.main.get().output + sourceSets.test.get().output
      args = listOf(
        "--plugin", "pretty",
        "--plugin", "html:build/reports/cucumber/cucumber-report.html",
        "--glue", "org.endeavourhealth.pipeline.inbound",
        "src/test/resources"
      )
    }
  }
}
