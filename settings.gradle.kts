rootProject.name = "pxpoc"
includeBuild("../../greenwood-server/services/common")

pluginManagement {
    repositories {
        maven("https://plugins.gradle.org/m2/")
        maven("https://repo.maven.apache.org/maven2")
        maven("https://www.mvnrepository.com")
    }
}