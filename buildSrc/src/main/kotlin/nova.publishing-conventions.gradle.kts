plugins {
  id("nova.common-conventions")
  `maven-publish`
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      from(components["java"])
    }
  }
}