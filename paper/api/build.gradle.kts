plugins {
    id("nova.paper-conventions")
    id("nova.publishing-conventions")
}

dependencies {
    api("org.ahocorasick:ahocorasick:0.6.3")
    compileOnlyApi(libs.luckperms)
    compileOnlyApi(libs.maia.paper)
    compileOnly(libs.spigot)
    compileOnly(libs.placeholderapi)
}