import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
}

val outputJsName = "my-vscode-extension"

kotlin {
    version = "0.0.1"

    js {
        nodejs()
        useCommonJs()
        binaries.library()
        outputModuleName.set(outputJsName)

        compilations.named("main").configure {
            packageJson {
                version = project.version.toString()

                // ensure that the consumer has at least this version of vscode installed
                customField("engines", mapOf("vscode" to "^1.107.0"))

                val commands =
                    listOf(
                        mapOf(
                            "command" to "helloworld.helloWorld",
                            "title" to "Hello World",
                        ),
                    )

                customField("activationEvents", listOf("onCommand:helloworld.helloWorld"))
                customField("categories", listOf("Other"))
                customField("contributes", mapOf("commands" to commands))
                customField("displayName", "HelloWorld")
                customField("description", "My first extension")
            }

            tasks.withType<KotlinJsCompile>().configureEach {
                compilerOptions {
                    sourceMap = false
                }
            }
        }

    }

    sourceSets {
        jsMain.dependencies {
            implementation(libs.kotlinExternals.vscode)
        }
    }
}




tasks {
    register<Copy>("buildAndCopyFrontend") {
        group = "build"
        description = "Builds & copies Compose JS frontend distribution to backend webapp directory"

        val frontendProject = project(":src:ui")

        val distDir = frontendProject.layout.buildDirectory.dir("dist/js/productionExecutable")

        val targetDir =
            layout.buildDirectory
                .dir("processedResources")
                .get()
                .dir("js/main/ui")
                .asFile

        dependsOn(frontendProject.tasks.named("jsBrowserDistribution"))

        logger.lifecycle("Copying Frontend. From: ${distDir.get().asFile}\tTo: $targetDir")

        from(distDir)
        into(targetDir)

        doFirst {
            if (targetDir.exists()) {
                targetDir.listFiles()?.forEach { it.deleteRecursively() }
            } else {
                targetDir.mkdirs()
            }
        }

        doLast {
            println("Frontend build artifacts copied to ${targetDir.absolutePath}")
        }
    }

    named<ProcessResources>("jsProcessResources") {
        dependsOn("buildAndCopyFrontend")
    }

    register<Exec>("debugExtension") {
        group = "vscode"
        dependsOn("jsNodeDevelopmentLibraryDistribution")

        val basePath =
            rootProject.layout.buildDirectory
                .dir("js/packages/${outputJsName}")
                .get()
        logger.lifecycle("Running VsCode Debug: $basePath")
        executable = if (Os.isFamily(Os.FAMILY_WINDOWS)) "code.cmd" else "code"
        args("--inspect-extensions=9229", "--extensionDevelopmentPath=$basePath")
    }

    register<Exec>("packageVsix") {
        group = "vscode"
        description = "Packages the VS Code extension into a .vsix"

        val extensionRoot =
            rootProject.layout.buildDirectory
                .dir("js/packages/${outputJsName}")
                .get()
                .asFile
        logger.lifecycle("Building VsCode Extension: $extensionRoot")
        val outputDir =
            layout.buildDirectory
                .dir("dist")
                .get()
                .asFile
        logger.lifecycle("VsCode Extension Output Dir: $outputDir")

        if (!outputDir.exists()) {
            outputDir.mkdirs()
        } else {
            outputDir.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    file.deleteRecursively()
                } else {
                    file.delete()
                }
            }
        }

        workingDir = extensionRoot
        commandLine = listOf("vsce", "package", "--no-dependencies", "-o", "${outputDir.path}")
        dependsOn("build", "buildAndCopyFrontend")
    }
}
