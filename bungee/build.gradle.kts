plugins {
    id("nova.paper-conventions")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("net.minecrell.plugin-yml.bungee") version "0.5.2"
}

repositories {
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    api(libs.maia.api)
    api("ml.stargirls:storage-redis-dist:1.0.0")
    api("ml.stargirls:storage-gson-dist:1.0.0")
    api("team.unnamed:inject:1.0.1")
    api("org.spongepowered:configurate-yaml:4.1.2")
    compileOnly("io.github.waterfallmc:waterfall-api:1.19-R0.1-SNAPSHOT")
}

bungee {
    name = "nova"
    main = "ml.stargirls.nova.bungee.NovaPlugin"
    description = "Bridge between spigot servers and velocity"
    author = "pixeldev"
}

tasks {
    shadowJar {
        archiveBaseName.set(project.name)
        archiveClassifier.set("")
        relocate("org.spongepowered.configurate", "ml.stargirls.nova.configurate")
    }
}