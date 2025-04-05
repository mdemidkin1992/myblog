plugins {
    java
    war
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.springWebMvc)
    implementation(libs.jakartaServlet)
    implementation(libs.h2database)
    implementation(libs.springDataJdbc)
    implementation(libs.springBootStarterThymeleaf)

    compileOnly(libs.lombok)

    annotationProcessor(libs.lombok)

    testRuntimeOnly(libs.junitJupiterEngine)

    testImplementation(libs.springTest)
    testImplementation(libs.junitJupiterApi)
    testImplementation(libs.hamcrest)
    testImplementation(libs.jaywayJsonPath)

    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
