import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
  id("java-library")
  id("jacoco")
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
  group = JavaBasePlugin.VERIFICATION_GROUP
  description = "Runs Cucumber Feature Files"
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

