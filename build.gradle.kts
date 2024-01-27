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
    maven {
        name = "Patbox"
        url = uri("https://maven.nucleoid.xyz/")
    }
    flatDir {
        dirs("E:\\Documents\\Mod Libraries\\ac\\build\\libs")
    }
    flatDir {
        dirs("E:\\Documents\\Mod Libraries\\fc\\build\\libs")
    }
    flatDir {
        dirs("E:\\Documents\\Mod Libraries\\gc\\build\\libs")
    }
    flatDir {
        dirs("E:\\Documents\\Mod Libraries\\fzzy_config\\build\\libs")
    }
    flatDir {
        dirs("E:\\Documents\\Mod Libraries\\structurized-reborn-1.0\\build\\libs")
    }
    flatDir {
        dirs("E:\\Documents\\Mod Libraries\\siht\\build\\libs")
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
    modCompileOnly ("dev.emi:emi-fabric:${emiVersion}:api")
    modLocalRuntime ("dev.emi:emi-fabric:${emiVersion}")

    val ccaVersion: String by project
    modCompileOnly("dev.onyxstudios.cardinal-components-api:cardinal-components-base:${ccaVersion}")
    modCompileOnly("dev.onyxstudios.cardinal-components-api:cardinal-components-entity:${ccaVersion}")

    val trinketsVersion: String by project
    modImplementation("dev.emi:trinkets:$trinketsVersion"){
        exclude("net.fabricmc.fabric-api")
    }

    val patchouliVersion: String by project
    modImplementation("vazkii.patchouli:Patchouli:$patchouliVersion"){
        exclude("net.fabricmc.fabric-api")
    }

    val structurizedVersion: String by project
    modImplementation(":structurized_reborn-$structurizedVersion"){
        exclude("net.fabricmc.fabric-api")
    }
    include(":structurized_reborn-$structurizedVersion")

    val acVersion: String by project
    modImplementation(":amethyst_core-$acVersion"){
        exclude("net.fabricmc.fabric-api")
    }

    val fcVersion: String by project
    modImplementation(":fzzy_core-$fcVersion"){
        exclude("net.fabricmc.fabric-api")
    }

    val gcVersion: String by project
    modImplementation(":gear_core-$gcVersion"){
        exclude("net.fabricmc.fabric-api")
    }

    val fzzyConfigVersion: String by project
    include(modImplementation(":fzzy_config-$fzzyConfigVersion"){
            exclude("net.fabricmc.fabric-api")
        }
    )

    val palVersion: String by project
    modImplementation("io.github.ladysnake:PlayerAbilityLib:$palVersion"){
        exclude("net.fabricmc.fabric-api")
    }
    //include("io.github.ladysnake:PlayerAbilityLib:$palVersion")

    val meVersion: String by project
    implementation("com.github.llamalad7.mixinextras:mixinextras-fabric:$meVersion")
    annotationProcessor("com.github.llamalad7.mixinextras:mixinextras-fabric:$meVersion")

    val cmVersion: String by project
    implementation("com.github.Fallen-Breath:conditional-mixin:$cmVersion")
    include("com.github.Fallen-Breath:conditional-mixin:$cmVersion")

    val cpaVersion: String by project
    modImplementation("eu.pb4:common-protection-api:$cpaVersion"){
        exclude("net.fabricmc.fabric-api")
    }
    //include("eu.pb4:common-protection-api:$cpaVersion")

    val lithiumVersion: String by project
    modImplementation("maven.modrinth:lithium:$lithiumVersion")

    val sihtVersion: String by project
    modImplementation(":should_i_hit_that-$sihtVersion"){
        exclude("net.fabricmc.fabric-api")
    }
    include(":should_i_hit_that-$sihtVersion")
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
    jar {
        from("LICENSE") { rename { "${base.archivesName.get()}_${it}" } }
    }
    jar {
        from( "credits.txt") { rename { "${base.archivesName.get()}_${it}" } }
    }
    processResources {
        val loaderVersion: String by project
        val fabricKotlinVersion: String by project
        val trinketsVersion: String by project
        val patchouliVersion: String by project
        val fcVersion: String by project
        val acVersion: String by project
        inputs.property("version", project.version)
        inputs.property("id", base.archivesName.get())
        inputs.property("loaderVersion", loaderVersion)
        inputs.property("fabricKotlinVersion", fabricKotlinVersion)
        inputs.property("trinketsVersion", trinketsVersion)
        inputs.property("patchouliVersion", patchouliVersion)
        inputs.property("fcVersion", fcVersion)
        inputs.property("acVersion", acVersion)
        filesMatching("fabric.mod.json") {
            expand(
                mutableMapOf(
                    "version" to project.version,
                    "id" to base.archivesName.get(),
                    "loaderVersion" to loaderVersion,
                    "fabricKotlinVersion" to fabricKotlinVersion,
                    "trinketsVersion" to trinketsVersion,
                    "patchouliVersion" to patchouliVersion,
                    "fcVersion" to fcVersion,
                    "acVersion" to acVersion))
        }
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
        embedded.project("trinkets")
        embedded.project("patchouli")
    }
    debugMode.set(false)
}
