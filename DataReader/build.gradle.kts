import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
  id("java")
  id("jacoco")
  alias(libs.plugins.spring.boot)
  alias(libs.plugins.sonar)
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

if (System.getenv("ENV") == "prod") {
  tasks.build {
    finalizedBy("sonar")
  }
}

sonar.properties {
  property("sonar.token", System.getenv("SONAR_LOGIN"))
  property("sonar.gradle.skipCompile", true)
  property("sonar.organization", "endeavourhealth-discovery")
  property("sonar.projectKey", "IMInboundPipeline_Queuereader")
  property("sonar.projectName", "DataReader")
  property("sonar.host.url", "https://sonarcloud.io")
  property("sonar.coverage.exclusions", "**/config/**, **/listener/**, **/model/**,")
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
