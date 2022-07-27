import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.1"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"

	application
}

group = "greenwood"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	api("io.github.microutils:kotlin-logging:2.1.21")
	api("org.springframework.boot:spring-boot-starter-actuator")


	// Align versions of all Kotlin components
	implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

	// Use the Kotlin JDK 11 standard library.
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("greenwood:common:0.0.1-SNAPSHOT"){
		//We've added Redis to Common (maybe we should reconsider this?), but no need to load it for this project
		exclude(group="org.springframework.boot", module= "spring-boot-starter-data-redis")
	}

	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("org.springframework.security:spring-security-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")


	implementation("com.squareup.okhttp3:okhttp:4.9.1")
	implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")
	implementation("com.squareup.okhttp3:mockwebserver:4.9.1")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test")
	testImplementation("io.kotlintest:kotlintest-runner-junit5:3.4.2")


	//implementation("org.keycloak.bom:keycloak-adapter-bom:18.0.2")
	//implementation("org.keycloak:keycloak-spring-boot-starter:18.0.2")
}

//dependencyManagement {
//	imports {
//		mavenBom "org.keycloak.bom:keycloak-adapter-bom"
//	}
//}
tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
