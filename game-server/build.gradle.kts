val springVersion: String by project

plugins {
    application
    kotlin("jvm")
    kotlin("plugin.spring") version "1.3.72"
    kotlin("plugin.jpa") version "1.3.72"
    id("org.springframework.boot") version "2.2.6.RELEASE"
    id("io.spring.dependency-management") version "1.0.7.RELEASE"
}

application {
    mainClass.set("net.dodian.uber.game.Server")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.11.0.rc1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.11.0.rc1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.apache.commons:commons-compress:1.20")
    implementation("org.quartz-scheduler:quartz:2.3.2")
    implementation("mysql:mysql-connector-java:8.0.24")
}