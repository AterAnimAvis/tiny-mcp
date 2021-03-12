
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

val generateV1 : Boolean
    get() = extra["generateV1"]?.toString()?.toBoolean() ?: false
val generateV2 : Boolean
    get() = extra["generateV2"]?.toString()?.toBoolean() ?: false

/* ======================================================================================================= Setup ==== */

group="com.github.ateranimavis"
version="1.0-SNAPSHOT"

/* ================================================================================================ Dependencies ==== */

repositories {
    maven {
        url = uri("https://files.minecraftforge.net/maven")
    }

    mavenCentral()
}

dependencies {
    implementation("net.minecraftforge:srgutils:0.4.1")
    implementation("org.jetbrains:annotations:20.1.+")
}

/* ======================================================================================================= Tasks ==== */

val mappingsJarV1 = File(".generated/mappings.jar")
val mappingsJarV2 = File(".generated/mappings-v2.jar")

val deleteMappings = tasks.register<Delete>("deleteMappings") {
    group = "Mappings"
    description = "Deletes the generated mappings"

    setDelete(mappingsJarV1)
    setDelete(mappingsJarV2)
}

val generateMappingsV1 = tasks.register<JavaExec>("generateMappingsV1") {
    dependsOn(deleteMappings, "build")
    group = "mappings"
    description = "Generates the v1 mappings"

    mainClass.set("com.github.ateranimavis.tiny_mcp.TinyV1")
    classpath = sourceSets.main.get().runtimeClasspath
    args = listOf(mcpType, mcpVersion, mappingsChannel, mappingsVersion, yarnVersion, yarnBuild)
}

val generateMappingsV2 = tasks.register<JavaExec>("generateMappingsV2") {
    dependsOn(deleteMappings, "build")
    group = "mappings"
    description = "Generates the v2 mappings"

    mainClass.set("com.github.ateranimavis.tiny_mcp.TinyV2")
    classpath = sourceSets.main.get().runtimeClasspath
    args = listOf(mcpType, mcpVersion, mappingsChannel, mappingsVersion, yarnVersion, yarnBuild)
}

val provideMappingsV1 = tasks.register("provideMappingsV1") {
    dependsOn(generateMappingsV1)
    group = "mappings"
    description = "Ensures the v1 mappings exists"

    doLast {
        if (!mappingsJarV1.exists()) throw AssertionError("TinyV1 mappings didn't generate")
    }
}

val provideMappingsV2 = tasks.register("provideMappingsV2") {
    dependsOn(generateMappingsV2)
    group = "mappings"
    description = "Ensures the v2 mappings exists"

    doLast {
        if (!mappingsJarV2.exists()) throw AssertionError("TinyV2 mappings didn't generate")
    }
}

val install = tasks.register("install") {
    dependsOn("publish")
    group = "mappings"
}

/* ================================================================================================== Publishing ==== */

configure<PublishingExtension> {
    publications {
        create<MavenPublication>("mappings") {
            groupId = "net.fabricmc"
            artifactId = "yarn"
            version = "$yarnVersion+build.$yarnBuild-mcp-$mappingsChannel-$mappingsVersion".replace("-", ".")

            if (generateV1) {
                artifact(mappingsJarV1) {
                    classifier = ""
                    builtBy(provideMappingsV1)
                }
            }

            if (generateV2) {
                artifact(mappingsJarV2) {
                    classifier = "v2"
                    builtBy(provideMappingsV2)
                }
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