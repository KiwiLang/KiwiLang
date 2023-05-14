// PLUGINS
// =======

plugins {
    kotlin("jvm") version "1.8.20"
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
}

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

// DOCUMENTATION

//val cleanDocumentation = task("cleanDocumentation") {
//    fileTree("${buildDir.path}/docs").files.forEach {
//        it.delete()
//    }
//}
//
//tasks.dokkaHtml.configure {
//    dependsOn(cleanDocumentation)
//    outputDirectory.set(buildDir.resolve("docs"))
//    cacheRoot.set(file("default"))
//    dokkaSourceSets {
//        named("docs") {
//            moduleName.set("My Dokka Project")
//            includes.from(fileTree("src/docs").include("**/*.md"))
//        }
//    }
//}
