plugins {
    `kotlin-dsl`
    id("org.openapi.generator") version "4.2.1"
    id("org.jetbrains.kotlin.jvm") version "1.3.61"
}
repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.openapitools:openapi-generator-gradle-plugin:4.0.3")
}