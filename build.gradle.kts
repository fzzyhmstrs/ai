plugins {
    id("fabric-loom")
    val kotlinVersion: String by System.getProperties()
    kotlin("jvm").version(kotlinVersion)
    id("com.modrinth.minotaur") version "2.+"
}
base {
    val archivesBaseName: String by project
    archivesName.set(archivesBaseName)
}
val log: File = file("changelog.md")
val mcVersions: String by project
val modVersion: String by project
version = modVersion
val mavenGroup: String by project
group = mavenGroup
println("## Changelog for Amethyst Imbuement $modVersion \n\n" + log.readText())
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
        name = "Progwml6 maven"
        url = uri("https://dvs1.progwml6.com/files/maven/")
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
        name = "Modrinth"
        url = uri("https://api.modrinth.com/maven")
        content {
            includeGroup("maven.modrinth")
        }
    }
    maven {
        name = "Jitpack"
        url = uri("https://jitpack.io")
    }
    flatDir {
        dirs("F:\\Documents\\Mod Libraries\\ac\\build\\libs")
    }
    flatDir {
        dirs("F:\\Documents\\Mod Libraries\\fc\\build\\libs")
    }
    flatDir {
        dirs("F:\\Documents\\Mod Libraries\\structurized-reborn-1.0\\build\\libs")
    }
    mavenCentral()
}
dependencies {
    val guavaVersion: String by project
    implementation("com.google.guava:guava:$guavaVersion")
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

    val reiVersion: String by project
    modCompileOnly("me.shedaniel:RoughlyEnoughItems-api:$reiVersion")
    modCompileOnly("me.shedaniel:RoughlyEnoughItems-api-fabric:$reiVersion")
    modRuntimeOnly("me.shedaniel:RoughlyEnoughItems-fabric:$reiVersion")

    val jeiVersion: String by project
    modImplementation("mezz.jei:$jeiVersion"){
        exclude ("mezz.jei")
    }

    val emiVersion: String by project
    modImplementation("dev.emi:emi:$emiVersion"){
        exclude("net.fabricmc.fabric-api")
    }

    val trinketsVersion: String by project
    modImplementation("dev.emi:trinkets:$trinketsVersion"){
        exclude("net.fabricmc.fabric-api")
    }

    val patchouliVersion: String by project
    modImplementation("vazkii.patchouli:Patchouli:$patchouliVersion"){
        exclude("net.fabricmc.fabric-api")
    }
    include("vazkii.patchouli:Patchouli:$patchouliVersion")

    val structurizedVersion: String by project
    modImplementation("maven.modrinth:Wd844r7Q:$structurizedVersion"){
        exclude("net.fabricmc.fabric-api")
    }
    include("maven.modrinth:Wd844r7Q:$structurizedVersion")

    val acVersion: String by project
    modImplementation(":amethyst_core-$acVersion"){
        exclude("net.fabricmc.fabric-api")
    }

    val fcVersion: String by project
    modImplementation(":fzzy_core-$fcVersion"){
        exclude("net.fabricmc.fabric-api")
    }

    val palVersion: String by project
    modImplementation("io.github.ladysnake:PlayerAbilityLib:$palVersion"){
        exclude("net.fabricmc.fabric-api")
    }
    include("io.github.ladysnake:PlayerAbilityLib:$palVersion")

    val meVersion: String by project
    implementation("com.github.LlamaLad7:MixinExtras:$meVersion")
    annotationProcessor("com.github.LlamaLad7:MixinExtras:$meVersion")
    include("com.github.LlamaLad7:MixinExtras:$meVersion")
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
        //withSourcesJar()
    }
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("amethyst-imbuement")
    versionNumber.set(modVersion)
    versionName.set("${base.archivesName.get()}-$modVersion")
    versionType.set("release")
    uploadFile.set(tasks.remapJar.get())
    gameVersions.addAll(mcVersions.split(","))
    loaders.addAll("fabric","quilt")
    detectLoaders.set(false)
    changelog.set("## Changelog for Amethyst Imbuement $modVersion \n\n" + log.readText())
    dependencies{
        required.project("fabric-api")
        required.project("fabric-language-kotlin")
        required.project("amethyst-core")
        required.project("fzzy-core")
        optional.project("emi")
        optional.project("roughly-enough-items")
        embedded.project("trinkets")
        embedded.project("patchouli")
    }
    debugMode.set(true)
}
