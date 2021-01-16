
/* ===================================================================================================== Plugins ==== */

plugins {
    id("java")
    id("application")
    `maven-publish`
}

/* ====================================================================================================== Config ==== */

val mcpType : String by extra
val mcpVersion : String by extra

val mappingsChannel : String by extra
val mappingsVersion : String by extra

val yarnVersion : String by extra
val yarnBuild : String by extra

/* ======================================================================================================= Setup ==== */

group="ateranimavis.mcp2yarn"
version="1.0-SNAPSHOT"

val mappingsJar = File(".generated/mappings.jar")

val deleteMappingsJar = tasks.register<Delete>("deleteMappingsJar") {
    group = "Mappings"

    setDelete(mappingsJar)
}

val generateMappingsJar = tasks.register<JavaExec>("generateMappingsJar") {
    dependsOn(deleteMappingsJar, "build")
    group = "mappings"
    description = "Generates the Mappings Jar"

    mainClass.set("ateranimavis.mcp2yarn.Main")
    classpath = sourceSets.main.get().runtimeClasspath
    args = listOf(mcpType, mcpVersion, mappingsChannel, mappingsVersion, yarnVersion, yarnBuild)
}

val provideMappingsJar = tasks.register("provideMappingsJar") {
    dependsOn(generateMappingsJar)
    group = "mappings"
    description = "Ensures the Mapping Jar exists"

    doLast {
        if (!mappingsJar.exists()) throw AssertionError("Mappings Jar doesn't exist")
    }
}

val install = tasks.register("install") {
    dependsOn("publish") // publishToMavenLocal
    group = "mappings"
}

/* ================================================================================================== Publishing ==== */

configure<PublishingExtension> {
    publications {
        create<MavenPublication>("mappings") {
            groupId = "net.fabricmc"
            artifactId = "yarn"
            version = "$yarnVersion+build.$yarnBuild-mcp-$mappingsChannel-$mappingsVersion".replace("-", ".")

            artifact(mappingsJar) {
                builtBy(provideMappingsJar)
            }
        }
    }
    repositories {
        mavenLocal()
    }
}

/* ===================================================================================================== Cleanup ==== */

tasks.named("run").get().group = "other"

/* =================================================================================================== Utilities ==== */

typealias Inject = javax.inject.Inject

open class SingleOutputTask @Inject constructor(@get:OutputFile val output: File) : DefaultTask()