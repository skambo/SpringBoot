import io.skambo.example.tasks.GenerateModelTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.format.DateTimeFormatter
import java.time.ZoneOffset
import java.time.Instant
import java.util.Properties
import java.nio.file.Files
import java.nio.file.Paths

val springBootVersion:String = "2.3.2.RELEASE"

plugins {
    id ("org.springframework.boot") version "2.3.2.RELEASE"
    id ("io.spring.dependency-management") version "1.0.8.RELEASE"
    id ("org.liquibase.gradle") version "2.0.2"
    kotlin("jvm") version "1.3.50"
    kotlin("plugin.spring") version "1.3.41"
    jacoco
    application
    checkstyle
}

apply(plugin = "kotlin-jpa")
apply(plugin = "org.openapi.generator")
apply(plugin = "liquibase")
apply(plugin = "application")
// apply(plugin = "checkstyle")

group = "io.skambo"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

val ktlintOnly: Configuration by configurations.creating

val liquibaseProperties = Properties()
Files.newInputStream(Paths.get("src/main/resources/liquibase.properties")).use {
    liquibaseProperties.load(it)
}

application {
    mainClassName = "io.skambo.example.SpringExampleApplication"
}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Main-Class"] = "io.skambo.example.SpringExampleApplication"
    }

    // To add all of the dependencies otherwise a "NoClassDefFoundError" error
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}


//val developmentOnly: Configuration by configurations.creating
//configurations {
// runtimeClasspath {
// extendsFrom(developmentOnly)
//  }
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
        classpath("org.liquibase:liquibase-gradle-plugin:2.0.1")
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

    implementation("org.springframework.boot:spring-boot-starter-web:$springBootVersion") {
        exclude(module = "spring-boot-starter-logging")
    }
    implementation("org.springframework.boot:spring-boot-starter-security:$springBootVersion") {
        exclude(module = "spring-boot-starter-logging")
    }
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:$springBootVersion") {
        exclude(module = "spring-boot-starter-logging")
    }
    implementation("mysql:mysql-connector-java")

    // logging dependency
    implementation("org.springframework.boot:spring-boot-starter-log4j2:$springBootVersion")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.0")

    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.9.5")

    // https://mvnrepository.com/artifact/org.apache.httpcomponents/httpclient
    compile("org.apache.httpcomponents:httpclient:4.5.13")

    implementation("com.google.code.gson:gson:2.8.5")

    // https://mvnrepository.com/artifact/org.springframework.kafka/spring-kafka
    implementation("org.springframework.kafka:spring-kafka:2.6.6")

    // https://mvnrepository.com/artifact/io.micrometer/micrometer-core
    implementation("io.micrometer:micrometer-core:1.6.4")

    // https://mvnrepository.com/artifact/io.micrometer/micrometer-registry-datadog
    implementation("io.micrometer:micrometer-registry-datadog:1.6.4")

    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-actuator
    implementation("org.springframework.boot:spring-boot-actuator:2.4.3")

    // https://mvnrepository.com/artifact/io.micrometer/micrometer-spring-legacy
    // implementation("io.micrometer:micrometer-spring-legacy:1.3.17")


    compile("org.liquibase:liquibase-core:4.2.2")
    compile("org.liquibase.ext:liquibase-hibernate5:4.2.2")
    compile("org.liquibase:liquibase-gradle-plugin:2.0.1")
    compile("mysql:mysql-connector-java:8.0.12")

    add("liquibaseRuntime", "org.liquibase:liquibase-core:4.2.2")
    add("liquibaseRuntime", "org.liquibase.ext:liquibase-hibernate5:4.2.2")
    add("liquibaseRuntime", "org.springframework.boot:spring-boot-starter-data-jpa:$springBootVersion")
    add("liquibaseRuntime", "org.liquibase:liquibase-gradle-plugin:2.0.1")
    add("liquibaseRuntime", "mysql:mysql-connector-java:8.0.12")
    add("liquibaseRuntime", "ch.qos.logback:logback-core:1.2.3")
    add("liquibaseRuntime", "ch.qos.logback:logback-classic:1.2.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootVersion") {
        exclude(module ="spring-boot-starter-logging")
    }

    // https://mvnrepository.com/artifact/org.hsqldb/hsqldb
    testCompile("org.hsqldb:hsqldb:2.5.1")

    // https://mvnrepository.com/artifact/net.javacrumbs.json-unit/json-unit-assertj
    testCompile("net.javacrumbs.json-unit:json-unit-assertj:2.22.1")

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
    ),
    mapOf(
        "name" to "generateUpdateUserDTO",
        "swaggerPath" to "$rootDir/definitions/update-user-1.yaml",
        "packageName" to "io.skambo.example.infrastructure.api.updateuser.v1.dto",
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
    testLogging {
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

task<Test>("unitTest") {
    useJUnitPlatform()
    exclude("io/skambo/example/integration/**")

    testLogging {
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

task<Test>("integrationTest") {
    useJUnitPlatform()
    filter {
        includeTestsMatching("io.skambo.example.integration.*")
    }

    testLogging {
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

checkstyle {
    toolVersion = "8.16"
    isIgnoreFailures = false
    isShowViolations = true
    configFile = rootProject.file("config/checkstyle/checkstyle.xml")
}

jacoco {
    toolVersion = "0.8.4"
}

tasks.jacocoTestReport {
    doFirst {

        val list: MutableList<String> = (project.ext.get("generatedFileNames") as MutableList<String>)
        sourceDirectories.setFrom(
            sourceSets.main.get().output.asFileTree.matching {
                // exclude main()
                exclude(list)
            }
        )

        // exclude generated classed
        val excluded: MutableList<String> = mutableListOf()
        list.forEach {
            excluded.add(it.substring(it.indexOf("io/skambo")).replace(".kt", "**"))
        }

        classDirectories.setFrom(
            sourceSets.main.get().output.asFileTree.matching {
                // exclude main()
                exclude(excluded)
            }
        )
    }

    reports {
        val mainSrc = "$rootDir/src/main/kotlin"
        val tree = fileTree("$rootDir/build/classes")

        sourceDirectories.setFrom(mainSrc)
        classDirectories.setFrom(files(tree))
        xml.isEnabled = true
        csv.isEnabled = false
        html.isEnabled = true
        xml.destination = file("$buildDir/reports/coverage/build.xml")
        html.destination = file("$buildDir/reports/coverage")
    }
}

tasks.jacocoTestCoverageVerification {
    doFirst {
        val list: MutableList<String> = (project.ext.get("generatedFileNames")
                as MutableList<String>)
        sourceDirectories.setFrom(
            sourceSets.main.get().output.asFileTree.matching {
                // exclude main()
                exclude(list)
            }
        )

        // exclude generated classed
        val excluded: MutableList<String> = mutableListOf<String>()
        list.forEach {
        }

        classDirectories.setFrom(
            sourceSets.main.get().output.asFileTree.matching {
                // exclude main()
                exclude(excluded)
            }
        )
    }
    violationRules {
        rule {
            limit {
                minimum = "0.7".toBigDecimal()
            }
        }
    }
}

val testCoverage by tasks.registering {
    group = "verification"
    description = "Runs the unit tests with coverage."

    dependsOn(":generateDTO", ":test", ":jacocoTestReport", ":jacocoTestCoverageVerification")
    val jacocoTestReport = tasks.findByName("jacocoTestReport")
    jacocoTestReport?.mustRunAfter(tasks.findByName("test"))
    tasks.findByName("jacocoTestCoverageVerification")?.mustRunAfter(jacocoTestReport)
}

liquibase {
    activities.register("main") {
        this.arguments = mapOf(
            "logLevel" to "debug",
            "changeLogFile" to "src/main/resources/db/changesets/${buildTimestamp()}.xml",
            "driver" to liquibaseProperties["liquibase.driver-class-name"],
            "referenceUrl" to liquibaseProperties["liquibase.reference-url"],
            "url" to liquibaseProperties["liquibase.url"],
            "username" to liquibaseProperties["liquibase.username"],
            "password" to liquibaseProperties["liquibase.password"]
        )
    }
    runList = "main"
}

fun buildTimestamp():String {
    return DateTimeFormatter
        .ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
        .withZone(ZoneOffset.UTC)
        .format(Instant.now())
}


