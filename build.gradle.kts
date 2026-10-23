plugins {
    kotlin("jvm") version "2.3.0"
    id("net.linguica.maven-settings") version "0.5" apply false
}
if (!providers.environmentVariable("CLOUD_AUTOUPDATE_BUILD").isPresent) {
    apply(plugin = "net.linguica.maven-settings")
}

group = "fr.nocsy"
version = "4.2.1"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        name = "gg-repo"
        setUrl("https://repo.griefergames.cloud/repository/maven-public/")
    }
    maven {
        name = "github"
        setUrl("https://maven.pkg.github.com/griefergames/repo")
    }
    maven {
        name = "papermc-repo"
        setUrl("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "lumine"
        setUrl("https://mvn.lumine.io/repository/maven-public/")
    }
    maven {
        name = "enginehub"
        setUrl("https://maven.enginehub.org/repo/")
    }
    maven {
        name = "placeholderapi"
        setUrl("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
    maven {
        name = "nexomc"
        setUrl("https://repo.nexomc.com/releases")
    }
    maven {
        name = "matteodev"
        setUrl("https://maven.devs.beer/")
    }
}

dependencies {
    compileOnly(ggcloud.kotlin.stdlib)
    compileOnly(ggcloud.kotlin.coroutines.core)
    compileOnly(ggcloud.kotlin.coroutines.jdk8)

    compileOnly("org.projectlombok:lombok:1.18.44")
    annotationProcessor("org.projectlombok:lombok:1.18.44")

    compileOnly(ggcloud.paper.api)

    compileOnly("io.lumine:Mythic-Dist:5.12.1")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.15")
    compileOnly("me.clip:placeholderapi:2.12.2")
    compileOnly("net.luckperms:api:5.5")
    compileOnly("com.ticxo.modelengine:ModelEngine:R4.0.9")
    compileOnly("io.github.toxicity188:bettermodel-bukkit-api:2.2.0")
    compileOnly("dev.lone:api-itemsadder:4.0.10")
    compileOnly("com.nexomc:nexo:1.21.0") {
        exclude(group = "net.byteflux", module = "libby-bukkit")
        exclude(group = "dev.triumphteam", module = "triumph-gui")
    }
}

tasks {
    jar {
        archiveBaseName.set("MCPets")
    }

    processResources {
        // Keep Maven-style ${project.name} / ${project.version} placeholders working
        filesMatching("plugin.yml") {
            expand(
                "project" to mapOf(
                    "name" to "MCPets",
                    "version" to version,
                )
            )
        }
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}
