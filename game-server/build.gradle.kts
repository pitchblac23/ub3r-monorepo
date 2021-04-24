import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val springVersion: String by project

plugins {
    application
    kotlin("jvm")
    kotlin("plugin.spring") version "1.3.72"
    kotlin("plugin.jpa") version "1.3.72"
    id("org.springframework.boot") version "2.2.6.RELEASE"
    id("io.spring.dependency-management") version "1.0.7.RELEASE"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.11.0.rc1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.11.0.rc1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.springframework.boot:spring-boot-starter-web:$springVersion")
    implementation("org.springframework.boot:spring-boot-starter-security:$springVersion")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:$springVersion")

    implementation("org.apache.commons:commons-compress:1.20")
    implementation("org.quartz-scheduler:quartz:2.3.2")

    runtimeOnly("mysql:mysql-connector-java")
    runtimeOnly("com.h2database:h2:1.4.200")
}