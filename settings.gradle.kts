rootProject.name = "nova"

pluginManagement {
  repositories {
    gradlePluginPortal()
    maven("https://repo.papermc.io/repository/maven-public/")
  }
}

includePrefixed("bungee")

arrayOf("api", "plugin").forEach {
  includePrefixed("paper:$it")
}

fun includePrefixed(name: String) {
  val kebabName = name.replace(':', '-')
  val path = name.replace(':', '/')
  val baseName = "${rootProject.name}-$kebabName"

  include(baseName)
  project(":$baseName").projectDir = file(path)
}