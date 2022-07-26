plugins {
    id("fabric-loom")
    val kotlinVersion: String by System.getProperties()
    kotlin("jvm").version(kotlinVersion)
}
base {
    val archivesBaseName: String by project
    archivesName.set(archivesBaseName)
}
val modVersion: String by project
version = modVersion
val mavenGroup: String by project
group = mavenGroup
repositories {
    maven {
        name = "TerraformersMC"
        url = uri("https://maven.terraformersmc.com/")
    }
    maven {
        name = "Ladysnake Libs"
        url = uri("https://ladysnake.jfrog.io/artifactory/mods")
    }
    maven {
        name = "REI"
        url = uri("https://maven.shedaniel.me")
    }
    maven {
        name = "Patchouli Lib"
        url = uri("https://maven.blamejared.com")
    }
    maven {
        name = "Modrinth"
        url = uri("https://api.modrinth.com/maven")
        content {
            includeGroup("maven.modrinth")
        }
    }
    flatDir {
        dirs("F:\\Documents\\Mod Libraries\\ac\\build\\libs")
    }
}
dependencies {
    val minecraftVersion: String by project
    minecraft("com.mojang:minecraft:$minecraftVersion")
    val yarnMappings: String by project
    mappings("net.fabricmc:yarn:$yarnMappings:v2")
    val loaderVersion: String by project
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
    val fabricVersion: String by project
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")
    val fabricKotlinVersion: String by project
    modImplementation("net.fabricmc:fabric-language-kotlin:$fabricKotlinVersion")

    modCompileOnly("me.shedaniel:RoughlyEnoughItems-api:8.0.442")
    modCompileOnly("me.shedaniel:RoughlyEnoughItems-api-fabric:8.0.442")
    modRuntimeOnly("me.shedaniel:RoughlyEnoughItems-fabric:8.0.442")

    modImplementation("dev.emi:emi:0.3.0+1.18.2")

    modImplementation("dev.emi:trinkets:3.3.0"){
        exclude("net.fabricmc.fabric-api")
    }
    include("dev.emi:trinkets:3.3.0")

    modImplementation("vazkii.patchouli:Patchouli:1.18.2-67-FABRIC"){
        exclude("net.fabricmc.fabric-api")
    }
    include("vazkii.patchouli:Patchouli:1.18.2-67-FABRIC")

    modImplementation("maven.modrinth:Wd844r7Q:1.18.2-02"){
        exclude("net.fabricmc.fabric-api")
    }
    include("maven.modrinth:Wd844r7Q:1.18.2-02")

    modImplementation("maven.modrinth:amethyst-core:0.2.0+1.18.2"){
        exclude("net.fabricmc.fabric-api")
    }
    include("maven.modrinth:amethyst-core:0.2.0+1.18.2")

    modImplementation("maven.modrinth:coloredglowlib:1.3.0"){
        exclude("net.fabricmc.fabric-api")
    }
    include("maven.modrinth:coloredglowlib:1.3.0")

    modImplementation("io.github.ladysnake:PlayerAbilityLib:1.5.0"){
        exclude("net.fabricmc.fabric-api")
    }
    include("io.github.ladysnake:PlayerAbilityLib:1.5.0")
}

tasks {
    val javaVersion = JavaVersion.VERSION_17
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
        options.release.set(javaVersion.toString().toInt())
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions { jvmTarget = javaVersion.toString() }
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
    }
    jar { from("LICENSE") { rename { "${it}_${base.archivesName}" } } }
    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") { expand(mutableMapOf("version" to project.version)) }
    }
    java {
        toolchain { languageVersion.set(JavaLanguageVersion.of(javaVersion.toString())) }
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        withSourcesJar()
    }
}
