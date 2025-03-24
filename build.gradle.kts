plugins {
    `spark-conventions`
}

scala {
    scalaVersion = "2.12.18"
}

dependencies {
    implementation("com.typesafe:config:1.4.3")
    implementation(spark("sql"))
    implementation(spark("catalyst"))
    implementation(spark("core"))
    implementation("org.scala-lang:scala-library")

    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks {
    test {
        useJUnitPlatform()
    }
}
