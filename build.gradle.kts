plugins {
  id("java")
}

repositories {
  gradlePluginPortal()
}

java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

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
