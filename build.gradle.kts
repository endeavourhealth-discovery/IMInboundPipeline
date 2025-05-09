plugins {
  id("java")
}

repositories {
  gradlePluginPortal()
}

java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

if (System.getenv("ENV") == "prod") {
  tasks.build {
    finalizedBy("publish")
  }
}

allprojects {
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
}
