import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.apache.avro") version "1.8.2"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    kotlin("plugin.jpa") version "1.9.22"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://packages.confluent.io/maven/")
        name = "confluent"
    }
}


dependencies {
    compileOnly("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.cloud:spring-cloud-stream-binder-kafka")
    testImplementation("org.springframework.cloud:spring-cloud-stream-schema")
    implementation("io.confluent:kafka-avro-serializer:5.3.0")
//    implementation("org.apache.avro:avro")
//    implementation("io.confluent:kafka-schema-registry")
    runtimeOnly("com.mysql:mysql-connector-j")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
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

//avro {
//    setCreateSetters(false)
//    outputCharacterEncoding.set("UTF-8")
//}

//schemaRegistry {
//    url.set("http://localhost:8083")
//    download {
//        subjectPattern(inputPattern = "schema.data-key", file = "com.example.springcdcdebeziumdemo")
//    }
//}

//val schemaRegistry = "http://localhost:8083" // 스키마 레지스트리 주소
//val downloadInputs = listOf(
//    "schema.data-key",
//    "schema.data-value"
//)
//val avroDestination = "org/main/avro" //avro 스키마가 저장될 프로젝트상의 위치
//schemaRegistry {
//    url.set(schemaRegistry)
//    download {
//        // 패턴에 해당하는 서브젝트(스키마)를 다운로드
//        downloadInputs.forEach {
//            subjectPattern(
//                inputPattern = it,
//                file = avroDestination
//            )
//        }
//    }
//}