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
        name = "REI"
        url = uri("https://maven.shedaniel.me")
    }
    maven {
        name = "Ladysnake Libs"
        url = uri("https://ladysnake.jfrog.io/artifactory/mods")
    }
    maven {
        name = "Patchouli Lib"
        url = uri("https://maven.blamejared.com")
    }
    maven {
        name = "GlowLib"
        url = uri("https://api.modrinth.com/maven")
    }
    flatDir {
        dirs("F:\\Documents\\Mod Development\\structurize")
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

    modCompileOnly("me.shedaniel:RoughlyEnoughItems-api-fabric:7.3.432")
    modRuntimeOnly("me.shedaniel:RoughlyEnoughItems-fabric:7.3.432")

    modImplementation ("dev.emi:trinkets:3.1.0")
    include("dev.emi:trinkets:3.1.0")

    modImplementation ("vazkii.patchouli:Patchouli:1.18-60-FABRIC")
    include("vazkii.patchouli:Patchouli:1.18-60-FABRIC")

    modImplementation (":structurized:1.4.0+1.18")
    include(":structurized:1.4.0+1.18")

    modImplementation ("maven.modrinth:coloredglowlib:1.0.1")
    include("maven.modrinth:coloredglowlib:1.0.1")

    modApi("io.github.ladysnake:PlayerAbilityLib:1.5.0")
    include("io.github.ladysnake:PlayerAbilityLib:1.5.0")
}
/*loom{
    accessWidener = file("src/main/resources/amethyst_imbuement.accesswidener")
}*/
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
