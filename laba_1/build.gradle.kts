plugins {
    kotlin("jvm") version "2.2.10"
    application
    kotlin("plugin.serialization") version "1.9.10"
}

application {
    mainClass = "org.example.MainKt"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaExec>() {
    standardInput = System.`in`
}

kotlin {
    jvmToolchain(21)
}