plugins {
  id("java")
  id("jacoco")
  alias(libs.plugins.sonar)
}

repositories {
  gradlePluginPortal()
}

java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

sonar {
  properties {
    property("sonar.token", System.getenv("SONAR_LOGIN"))
    property("sonar.host.url", "https://sonarcloud.io")
    property("sonar.organization", "endeavourhealth-discovery")
    property("sonar.projectKey", "IMInboundPipeline")
    property("sonar.projectName", "IM Inbound Pipeline")
  }
}


subprojects {
  apply(plugin = "java")
  apply(plugin = "jacoco")

  repositories {
    mavenLocal()
    mavenCentral()
    maven {
      url = uri("https://artifactory.endhealth.co.uk/repository/maven-releases")
    }
    maven {
      url = uri("https://artifactory.endhealth.co.uk/repository/maven-snapshots")
    }
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

  sonar {
    properties {
      property("sonar.sources", "src/main/java")
      property("sonar.tests", "src/test/java")
      property("sonar.junit.reportPaths", "build/test-results/test")
    }
  }
}

project(":CommonAPI") {
  sonar {
    properties {
      property("sonar.coverage.exclusions", "**/config/**, **/controller/**, **/errorhandling/**")
    }
  }
}

project(":DataReader") {
  sonar {
    properties {
      property("sonar.coverage.exclusions", "**/config/**, **/listener/**, **/model/**,")
    }
  }
}

project(":FileReader") {
  sonar {
    properties {
      property("sonar.coverage.exclusions", "**/config/**, **/controller/**, **/model,**")
    }
  }
}

project(":MessageAPI") {
  sonar {
    properties {
      property("sonar.coverage.exclusions", "**/config/**, **/controller/**, **/errorhandling/**")
    }
  }
}

project(":SystemAPI") {
  sonar {
    properties {
      property("sonar.coverage.exclusions", "**/config/**, **/controller/**, **/errorhandling/**")
    }
  }
}

project(":Transform") {
}
