plugins {
    id("java")
    id("dev.architectury.loom") version("1.10-SNAPSHOT")
    id("architectury-plugin") version("3.4-SNAPSHOT")
    kotlin("jvm") version "2.0.21"
}

group = "net.ajsdev"
version = "1.0.0"

val minecraft_version = "1.21.1"
val loader_version = "0.16.14"
val fabric_version = "0.116.0+1.21.1"
val cobblemon_version = "1.6.1+1.21.1"

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    silentMojangMappingsLicense()

    mixin {
        defaultRefmapName.set("mixins.${project.name}.refmap.json")
    }
}

repositories {
    mavenCentral()
    maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    maven("https://maven.impactdev.net/repository/development/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://maven.nucleoid.xyz")
}

dependencies {
    minecraft("net.minecraft:minecraft:$minecraft_version")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:$loader_version")

    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabric_version")
    modImplementation(fabricApi.module("fabric-command-api-v2", fabric_version))

    modImplementation("net.fabricmc:fabric-language-kotlin:1.12.3+kotlin.2.0.21")
    modImplementation("com.cobblemon:fabric:$cobblemon_version")

    modImplementation("eu.pb4:sgui:1.6.1+1.21.1")
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand(project.properties)
    }
}

// ✅ Java 21 support
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

// ✅ Kotlin should target JVM 21
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "21"
    }
}
