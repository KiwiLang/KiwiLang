// PLUGINS
// =======

plugins {
    kotlin("jvm") version "1.8.20"
    kotlin("kapt") version "1.8.20"
    application
}

// APPLICATION
// ===========

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jline:jline:3.23.0")
    implementation(kotlin("reflect"))
    annotationProcessor("com.google.auto.service:auto-service:1.0-rc7")
    compileOnly("com.google.auto.service:auto-service:1.0-rc7")
//    kapt("org.kiwilang.preprocessor:")
}

kapt {

    javacOptions {
        option("-Xmaxerrs", 500)
    }
}

sourceSets

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}