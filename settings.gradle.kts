pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "net.griefergames.ggframe.gradle") {
                useModule("ggframe-gradle-plugin:${requested.version}")
            }
        }
    }
    repositories {
        maven("https://repo.griefergames.cloud/repository/maven-public/")
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositories {
        maven {
            name = "dependency-catalogs"
            url = uri("https://repo.griefergames.cloud/repository/dependency-catalogs/")
        }
        gradlePluginPortal()
    }
    versionCatalogs {
        create("ggcloud") {
            from("net.griefergames.ggcloud:cloud-dependency-catalog:${providers.gradleProperty("cloud.version").get()}-SNAPSHOT")
        }
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
rootProject.name = "mcpets"
