plugins {
	java
	id("org.springframework.boot") version "3.0.5"
	id("io.spring.dependency-management") version "1.1.0"
}

group = "cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_19

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.jetbrains:annotations:23.0.0")
    compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.mysql:mysql-connector-j")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.4") //OPEN API AMB SWAGGER PER SPRING MVC
	implementation("org.modelmapper:modelmapper:3.1.1") //MODELMAPPER LIBRARY
	implementation("org.springframework.boot:spring-boot-starter-security:3.0.5") //SPRING BOOT STARTER SECURITY
	implementation("io.jsonwebtoken:jjwt-api:0.11.5") //JWT LIBRARY
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5") //JWT LIBRARY
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5") //JWT LIBRARY
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2") //JUNIT TESTING
}

tasks.withType<Test> {
	useJUnitPlatform()
}
