import io.skambo.example.tasks.GenerateModelTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id ("org.springframework.boot") version "2.3.2.RELEASE"
    id ("io.spring.dependency-management") version "1.0.8.RELEASE"
    kotlin("jvm") version "1.3.50"
    kotlin("plugin.spring") version "1.3.41"
}

apply(plugin = "kotlin-jpa")
apply(plugin = "org.openapi.generator")

group = "io.skambo"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11


val ktlintOnly: Configuration by configurations.creating

//val developmentOnly: Configuration by configurations.creating
//configurations {
//    runtimeClasspath {
//        extendsFrom(developmentOnly)
//    }
//    compileOnly {
//        extendsFrom(configurations.annotationProcessor.get())
//    }
//}

buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-noarg:1.3.61")
        classpath("org.springframework.boot:spring-boot-gradle-plugin")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/com.squareup.moshi/moshi
    compile("com.squareup.moshi:moshi:1.11.0")

    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(module = "spring-boot-starter-logging")
    }
    implementation("org.springframework.boot:spring-boot-starter-security:2.3.5.RELEASE") {
        exclude(module = "spring-boot-starter-logging")
    }
    implementation("org.springframework.boot:spring-boot-starter-data-jpa") {
        exclude(module = "spring-boot-starter-logging")
    }
    implementation("mysql:mysql-connector-java")

    // developmentOnly("org.springframework.boot:spring-boot-devtools")

    // logging dependency
    implementation("org.springframework.boot:spring-boot-starter-log4j2:2.1.6.RELEASE")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.0")

    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.9.5")

    // testCompile("junit:junit:4.12")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "org.junit.vintage:junit-vintage-engine")
        exclude(module ="spring-boot-starter-logging")
    }

    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.3.2")
}

project.ext.set("generatedFileNames", mutableListOf<String>())

// Open API Generator options
val generatorConfigOptions = mapOf(
    "dateLibrary" to "java8",
    "useBeanValidation" to "false",
    "hideGenerationTimestamp" to "true"
)

val generatorTypeMappings = mapOf(
    "java.time.LocalDate" to "java.util.Date",
    "java.time.LocalDateTime" to "java.time.OffsetDateTime"
)

val headerImport = "io.skambo.example.infrastructure.api.common.dto.v1.Header"
val userDtoImport = "io.skambo.example.infrastructure.api.common.dto.v1.UserDTO"
val generatorImportMappings = mapOf(
    "Header" to headerImport,
    headerImport to headerImport, // hack to prevent wrong import of Header
    "Batch" to "io.skambo.example.infrastructure.api.common.dto.v1.Batch",
    "Status" to "io.skambo.example.infrastructure.api.common.dto.v1.Status",
    "Amount" to "io.skambo.example.infrastructure.api.common.dto.v1.Amount",
    "UserDTO" to userDtoImport,
    userDtoImport to userDtoImport
)

var generateMappings = listOf(
    mapOf(
        "name" to "generateCommonDTOs",
        "swaggerPath" to "$rootDir/definitions/common/common-1.yaml",
        "packageName" to "io.skambo.example.infrastructure.api.common.dto.v1"
    ),
    mapOf(
        "name" to "generateUserDTO",
        "swaggerPath" to "$rootDir/definitions/common/user-1.yaml",
        "packageName" to "io.skambo.example.infrastructure.api.common.dto.v1"
    ),
    mapOf(
        "name" to "generateApiErrorResponseDTO",
        "swaggerPath" to "$rootDir/definitions/common/api-error-response-1.yaml",
        "packageName" to "io.skambo.example.infrastructure.api.common.dto.v1",
        "importMappings" to generatorImportMappings
    ),
    mapOf(
        "name" to "generateGreetingDTO",
        "swaggerPath" to "$rootDir/definitions/greeting-1.yaml",
        "packageName" to "io.skambo.example.infrastructure.api.greeting.v1.dto",
        "importMappings" to generatorImportMappings
    ),
    mapOf(
        "name" to "generateCreateUserDTO",
        "swaggerPath" to "$rootDir/definitions/create-user-1.yaml",
        "packageName" to "io.skambo.example.infrastructure.api.createuser.v1.dto",
        "importMappings" to generatorImportMappings
    ),
    mapOf(
        "name" to "generateDeleteUserDTO",
        "swaggerPath" to "$rootDir/definitions/delete-user-1.yaml",
        "packageName" to "io.skambo.example.infrastructure.api.deleteuser.v1.dto",
        "importMappings" to generatorImportMappings
    ),
    mapOf(
        "name" to "generateFetchUserDTO",
        "swaggerPath" to "$rootDir/definitions/fetch-user-1.yaml",
        "packageName" to "io.skambo.example.infrastructure.api.fetchuser.v1.dto",
        "importMappings" to generatorImportMappings
    ),
    mapOf(
        "name" to "generateFetchUsersDTO",
        "swaggerPath" to "$rootDir/definitions/fetch-users-1.yaml",
        "packageName" to "io.skambo.example.infrastructure.api.fetchusers.v1.dto",
        "importMappings" to generatorImportMappings
    )
)

var generatorNames = mutableListOf<String>()
generateMappings.forEach {

    val name: String = it["name"] as String
    val swaggerPath: String = it["swaggerPath"] as String
    val packageName: String = it["packageName"] as String
    var importMapping: Map<String, String>? = mutableMapOf();

    if (it["importMappings"] != null) {
        importMapping = it["importMappings"] as Map<String, String>
    }

    tasks.register<GenerateModelTask>(name) {
        generatorName.value("kotlin")
        inputSpec.value(swaggerPath)
        outputDir.value("$rootDir")
        modelPackage.value(packageName)
        generateApiTests.value(false)
        generateModelTests.value(false)
        generateApiDocumentation.value(false)
        generateModelDocumentation.value(false)
        validateSpec.value(false)
        configOptions.value(generatorConfigOptions)
        logToStderr.value(false)
        importMappings.value(importMapping)
        typeMappings.value(generatorTypeMappings)
        generateAliasAsModel.value(false)
        doLast {
            (project.ext.get("generatedFileNames") as MutableList<String>)
                .addAll((this as GenerateModelTask).generatedFileNames)
        }
    }

    generatorNames.add(name)
}

tasks.register("generateDTO") {
    dependsOn(generatorNames)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks.test {
    useJUnitPlatform()
}

