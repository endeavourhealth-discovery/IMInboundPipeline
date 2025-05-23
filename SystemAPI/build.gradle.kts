import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
  id("war")
  id("jacoco")
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
