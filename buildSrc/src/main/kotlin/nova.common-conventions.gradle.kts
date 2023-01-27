plugins {
  `java-library`
}

repositories {
  mavenLocal()
  mavenCentral()
}

configure<JavaPluginExtension> {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
  }
}

tasks {
  compileJava {
    options.compilerArgs.add("-parameters")
  }
}