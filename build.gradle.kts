import com.github.imflog.schema.registry.Subject
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
    id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1"
    id("com.github.imflog.kafka-schema-registry-gradle-plugin") version "1.13.0"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    kotlin("plugin.jpa") version "1.9.22"
    kotlin("kapt") version "1.9.22"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven {
        url = uri("https://packages.confluent.io/maven/")
        name = "confluent"
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // avro + kafka schema registry
    implementation("org.apache.avro:avro:1.11.3")
    implementation("io.confluent:kafka-avro-serializer:7.4.2")
    implementation("io.confluent:kafka-schema-registry:7.4.2")

    runtimeOnly("com.mysql:mysql-connector-j")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    // Query DSL
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    implementation("com.querydsl:querydsl-core:5.0.0")
    compileOnly("com.querydsl:querydsl-core:5.0.0")
    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")
}

avro {
    // https://github.com/davidmc24/gradle-avro-plugin
    setOutputCharacterEncoding("UTF-8")
    setCreateSetters(false)
    setFieldVisibility("PRIVATE")
}

schemaRegistry {
    // https://github.com/ImFlog/schema-registry-plugin
    url.set("http://localhost:8081")
    pretty = true
    val userSubject = Subject("UserE", "src/main/avro/schema.avsc", "AVRO")
        .addLocalReference("UserE", "src/main/avro/schema.avsc")

    register {
        subject(userSubject)
    }
    compatibility {
        subject(userSubject)
    }
}

configurations {
    all {
//        exclude("org.springframework.boot", "spring-boot-starter-logging")
        exclude("org.apache.logging.log4j", "log4j-to-slf4j")
        exclude("ch.qos.logback", "logback-classic")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
