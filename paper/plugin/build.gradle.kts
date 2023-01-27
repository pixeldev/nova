plugins {
    id("nova.paper-conventions")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
    id("io.papermc.paperweight.userdev") version "1.3.8"
}

bukkit {
    name = "nova"
    apiVersion = "1.13"
    main = "ml.stargirls.nova.paper.NovaPlugin"
    description =
        "Core responsible for synchronizing the data between clusters"
    authors = listOf("pixeldev")
    depend = listOf("PlaceholderAPI", "LuckPerms")
}

tasks {
    shadowJar {
        archiveBaseName.set(rootProject.name)
        archiveClassifier.set("")
    }

    assemble {
        dependsOn(reobfJar)
    }
}

dependencies {
    paperDevBundle("1.19.2-R0.1-SNAPSHOT")
    api(project(":nova-paper-api"))
    compileOnly(libs.maia.paper)
    compileOnly(libs.placeholderapi)
    compileOnly(libs.spigot)
}