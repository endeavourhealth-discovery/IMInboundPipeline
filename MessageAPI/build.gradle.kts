import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
  id("war")
  id("jacoco")
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
