/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle

import org.gradle.testkit.runner.BuildResult
import org.gradle.util.GradleVersion
import org.jetbrains.kotlin.gradle.targets.wasm.binaryen.BinaryenEnvSpec
import org.jetbrains.kotlin.gradle.targets.js.dsl.Distribution
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmProject
import org.jetbrains.kotlin.gradle.targets.js.npm.fromSrcPackageJson
import org.jetbrains.kotlin.gradle.testbase.*
import org.jetbrains.kotlin.gradle.util.replaceText
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.condition.OS
import java.nio.file.Files
import kotlin.io.path.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@MppGradlePluginTests
class KotlinWasmGradlePluginIT : KGPBaseTest() {

    @DisplayName("Check wasi target")
    @GradleTest
    fun wasiTarget(gradleVersion: GradleVersion) {
        project("new-mpp-wasm-wasi-test", gradleVersion) {
            build(":wasmWasiTest") {
                assertTasksExecuted(":kotlinWasmNodeJsSetup")
                assertTasksExecuted(":compileKotlinWasmWasi")
                assertTasksExecuted(":wasmWasiNodeTest")
            }

            build(":wasmWasiTest") {
                assertTasksUpToDate(":kotlinWasmNodeJsSetup", ":compileKotlinWasmWasi", ":wasmWasiNodeTest")
            }

            projectPath.resolve("src/wasmWasiTest/kotlin/Test.kt").modify {
                it.replace(
                    "fun test2() = assertEquals(foo(), 2)",
                    "fun test2() = assertEquals(foo(), 2)" + "\n" +
                            """
                            @Test
                            fun test3() = assertEquals(foo(), 3)
                            """
                )
            }

            buildAndFail(":wasmWasiTest") {
                assertTasksUpToDate(":compileKotlinWasmWasi")
                assertTasksFailed(":wasmWasiNodeTest")
            }

            build(":wasmWasiNodeProductionRun") {
                assertTasksExecuted(":compileProductionExecutableKotlinWasmWasi")
                assertTasksExecuted(":compileProductionExecutableKotlinWasmWasiOptimize")
            }
        }
    }

    @DisplayName("Check js target")
    @GradleTest
    fun jsTarget(gradleVersion: GradleVersion) {
        project("new-mpp-wasm-test", gradleVersion) {
            buildGradleKts.modify {
                it.replace("<JsEngine>", "nodejs")
            }

            kotlinSourcesDir("wasmJsTest").resolve("Test.kt").writeText(
                """
                    package my.pack.name

                    import kotlin.test.Test
                    import kotlin.test.assertEquals

                    class WasmTest {
                        @Test
                        fun test1() = assertEquals(foo(), 2)
                    }
                """.trimIndent()
            )

            build("build") {
                assertTasksExecuted(":kotlinWasmNodeJsSetup")
                assertTasksExecuted(":compileKotlinWasmJs")
                assertTasksExecuted(":wasmJsNodeTest")
            }

            build(":wasmJsTest") {
                assertTasksUpToDate(":kotlinWasmNodeJsSetup", ":compileKotlinWasmJs", ":wasmJsNodeTest")
            }
        }
    }

    @DisplayName("Check wasi target run")
    @GradleTest
    fun wasiRun(gradleVersion: GradleVersion) {
        project("new-mpp-wasm-wasi-test", gradleVersion) {
            build(":wasmWasiNodeDevelopmentRun") {
                assertOutputContains("Hello from Wasi")
            }
        }
    }

    @DisplayName("Check wasi and js target")
    @GradleTest
    fun wasiAndJsTarget(gradleVersion: GradleVersion) {
        project("new-mpp-wasm-wasi-js-test", gradleVersion) {

            build("assemble") {
                assertTasksExecuted(":app:compileKotlinWasmWasi")
                assertTasksExecuted(":app:compileKotlinWasmJs")

                assertTasksExecuted(":lib:compileKotlinWasmWasi")
                assertTasksExecuted(":lib:compileKotlinWasmJs")
            }
        }
    }

    @DisplayName("Check wasi target with binaryen")
    @GradleTest
    fun wasiTargetWithBinaryen(gradleVersion: GradleVersion) {
        project("new-mpp-wasm-wasi-test", gradleVersion) {
            buildGradleKts.modify {
                it.replace("wasmWasi {", "wasmWasi {\nbinaries.executable()")
            }

            build("assemble") {
                assertTasksExecuted(":compileProductionExecutableKotlinWasmWasi")
                assertTasksExecuted(":compileProductionExecutableKotlinWasmWasiOptimize")

                val original =
                    projectPath.resolve("build/compileSync/wasmWasi/main/productionExecutable/kotlin/new-mpp-wasm-wasi-test.wasm")
                val optimized =
                    projectPath.resolve("build/compileSync/wasmWasi/main/productionExecutable/optimized/new-mpp-wasm-wasi-test.wasm")
                assertTrue {
                    Files.size(original) > Files.size(optimized)
                }
            }
        }
    }

    @DisplayName("Check js target with binaryen")
    @GradleTest
    fun jsTargetWithBinaryen(gradleVersion: GradleVersion) {
        project("new-mpp-wasm-js", gradleVersion) {
            buildGradleKts.modify {
                it.replace("<JsEngine>", "d8")
            }

            build("assemble") {
                assertTasksExecuted(":compileProductionExecutableKotlinWasmJs")
                assertTasksExecuted(":compileProductionExecutableKotlinWasmJsOptimize")

                val original =
                    projectPath.resolve("build/compileSync/wasmJs/main/productionExecutable/kotlin/redefined-wasm-module-name.wasm")
                val optimized =
                    projectPath.resolve("build/compileSync/wasmJs/main/productionExecutable/optimized/redefined-wasm-module-name.wasm")
                assertTrue {
                    Files.size(original) > Files.size(optimized)
                }
            }

            build(":wasmJsD8ProductionRun") {
                assertTasksUpToDate(":compileProductionExecutableKotlinWasmJs")
                assertTasksUpToDate(":compileProductionExecutableKotlinWasmJsOptimize")
                assertTasksExecuted(":wasmJsD8ProductionRun")
            }

            projectPath.resolve("src/wasmJsMain/kotlin/foo.kt").modify {
                it.replace(
                    "println(foo())",
                    """println("Hello from Wasi")"""
                )
            }

            build(":wasmJsD8ProductionRun") {
                assertTasksExecuted(":compileProductionExecutableKotlinWasmJs")
                assertTasksExecuted(":compileProductionExecutableKotlinWasmJsOptimize")
                assertTasksExecuted(":wasmJsD8ProductionRun")
            }
        }
    }

    @DisplayName("Check js target with browser")
    @GradleTest
    fun jsTargetWithBrowser(gradleVersion: GradleVersion) {
        project("new-mpp-wasm-js", gradleVersion) {
            buildGradleKts.modify {
                it.replace("<JsEngine>", "browser")
            }

            build("assemble") {
                assertTasksExecuted(":compileProductionExecutableKotlinWasmJs")
                assertTasksExecuted(":compileProductionExecutableKotlinWasmJsOptimize")
                assertTasksExecuted(":wasmJsBrowserDistribution")

                assertFileInProjectExists("build/${Distribution.DIST}/wasmJs/productionExecutable/new-mpp-wasm-js.js")
                assertFileInProjectExists("build/${Distribution.DIST}/wasmJs/productionExecutable/new-mpp-wasm-js.js.map")

                assertTrue("Expected one wasm file") {
                    projectPath.resolve("build/${Distribution.DIST}/wasmJs/productionExecutable").toFile().listFiles()!!
                        .filter { it.extension == "wasm" }
                        .size == 1
                }
            }

            projectPath.resolve("src/wasmJsMain/kotlin/foo.kt").replaceText(
                "actual fun foo(): Int = 2",
                "actual fun foo(): Int = 3",
            )

            build("assemble") {
                assertTasksExecuted(":compileProductionExecutableKotlinWasmJs")
                assertTasksExecuted(":compileProductionExecutableKotlinWasmJsOptimize")
                assertTasksExecuted(":wasmJsBrowserDistribution")

                assertFileInProjectExists("build/${Distribution.DIST}/wasmJs/productionExecutable/new-mpp-wasm-js.js")
                assertFileInProjectExists("build/${Distribution.DIST}/wasmJs/productionExecutable/new-mpp-wasm-js.js.map")

                assertTrue("Expected one wasm file") {
                    projectPath.resolve("build/${Distribution.DIST}/wasmJs/productionExecutable").toFile().listFiles()!!
                        .filter { it.extension == "wasm" }
                        .size == 1
                }
            }
        }
    }

    @DisplayName("Check mix project with wasi only dependency works correctly")
    @GradleTest
    fun jsAndWasiTargetsWithDependencyOnWasiOnlyProject(gradleVersion: GradleVersion) {
        project("wasm-wasi-js-with-wasi-only-dependency", gradleVersion) {

            build("build") {
                assertTasksExecuted(":lib:compileKotlinWasmWasi")
                assertTasksExecuted(":app:compileProductionExecutableKotlinWasmJs")
                assertTasksExecuted(":app:compileProductionExecutableKotlinWasmWasi")
            }
        }
    }

    @DisplayName("Wasi library")
    @GradleTest
    fun wasiLibrary(gradleVersion: GradleVersion) {
        project("wasm-wasi-library", gradleVersion) {

            build(":build") {
                assertTasksExecuted(":compileProductionLibraryKotlinWasmWasi")
                assertTasksExecuted(":compileKotlinWasmWasi")
                assertTasksExecuted(":wasmWasiNodeTest")
                assertTasksExecuted(":wasmWasiNodeProductionLibraryDistribution")

                val dist = "build/dist/wasmWasi/productionLibrary"
                assertFileExists(projectPath.resolve("$dist/foo.txt"))
                assertFileExists(projectPath.resolve("$dist/wasm-wasi-library.wasm"))
                assertFileExists(projectPath.resolve("$dist/wasm-wasi-library.wasm.map"))
                assertFileExists(projectPath.resolve("$dist/wasm-wasi-library.mjs"))
            }
        }
    }


    @DisplayName("Browser print works with null type")
    @GradleTest
    @OsCondition(
        supportedOn = [OS.LINUX, OS.MAC, OS.WINDOWS],
        enabledOnCI = []
    ) // The CI can't run the test, so, ignore it until we add headless Chrome browser to our CI
    fun testBrowserNullPrint(gradleVersion: GradleVersion) {
        project("kt-63230", gradleVersion) {
            build("check", "-Pkotlin.tests.individualTaskReports=true") {
                assertTestResults(projectPath.resolve("TEST-wasm.xml"), "wasmJsBrowserTest")
            }
        }
    }

    @DisplayName("Wasm JS variant does not contain file:// in webpack and works in Node.JS")
    @GradleTest
    fun wasmJsImportMetaUrlLibrary(gradleVersion: GradleVersion) {
        project("mpp-wasm-js-browser-nodejs", gradleVersion) {
            build(":assemble", ":wasmJsNodeTest") {
                assertTasksExecuted(":compileProductionExecutableKotlinWasmJs")
                assertTasksExecuted(":compileKotlinWasmJs")
                assertTasksExecuted(":wasmJsNodeTest")
                assertTasksExecuted(":wasmJsBrowserDistribution")

                val dist = "build/dist/wasmJs/productionExecutable"
                val uninstantiatedFile = projectPath.resolve("$dist/mpp-wasm-js-browser-nodejs.js")
                assertFileExists(uninstantiatedFile)
                assertFileDoesNotContain(uninstantiatedFile, "file://")
            }
        }
    }

    @DisplayName("Wasm sync task copies main compilation resources to test NPM package")
    @GradleTest
    fun wasmSyncTaskCopiesMainResourcesToTest(gradleVersion: GradleVersion) {
        project("mpp-wasm-js-browser-nodejs", gradleVersion) {
            build(":wasmJsNodeTest") {
                assertTasksExecuted(":wasmJsNodeTest")
                assertTasksExecuted(":wasmJsTestTestDevelopmentExecutableCompileSync")

                val packageDir = "build/wasm/packages/mpp-wasm-js-browser-nodejs-test/kotlin"
                assertFileExists(projectPath.resolve("$packageDir/data.json"))
                assertFileExists(projectPath.resolve("$packageDir/data-test.json"))
            }
        }
    }

    @DisplayName("Test compilation package.json generation depends on main package.json generation")
    @GradleTest
    fun testPackageJsonDependsOnMainPackageJson(gradleVersion: GradleVersion) {
        project("mpp-wasm-js-browser-nodejs", gradleVersion) {
            build(":wasmRootPackageJson") {
                assertTasksExecuted(":wasmJsPackageJson")
                assertTasksExecuted(":wasmJsTestPackageJson")
                assertTasksAreNotInTaskGraph(":compileKotlinWasmJs")
                assertTasksAreNotInTaskGraph(":compileTestKotlinWasmJs")
            }
        }
    }

    @DisplayName("Extracting NPM dependencies from transitive dependency")
    @GradleTest
    fun testExtractingNpmDependenciesFromTransitive(gradleVersion: GradleVersion) {
        project("mpp-wasm-published-library", gradleVersion) {
            val settingsGradleText = settingsGradleKts.readText()
            settingsGradleKts.append("include(\":library\")")
            build(":library:publish") {
                assertTasksExecuted(
                    ":library:compileKotlinWasmJs"
                )

                val moduleDir = subProject("library")
                    .projectPath
                    .resolve("repo/com/example/mpp-wasm-published-library/library-wasm-js/0.0.1/")

                val publishedKlib = moduleDir.resolve("library-wasm-js-0.0.1.klib")

                assertFileExists(publishedKlib)
            }

            build("clean")

            settingsGradleKts.writeText(settingsGradleText)
            settingsGradleKts.append("include(\":app\")")
            settingsGradleKts.append(
                """
                dependencyResolutionManagement {
                    repositories {
                        maven(rootDir.resolve("library/repo").toURI())
                    }
                }
                """.trimIndent()
            )

            build(":wasmRootPackageJson") {
                assertTasksExecuted(
                    ":wasmRootPackageJson"
                )

                val moduleDir = projectPath
                    .resolve("build/wasm/packages_imported/Kotlin-DateTime-library-kotlinx-datetime-wasm-js/0.6.0")

                val kotlinxDatetimePackageJson = moduleDir.resolve("package.json")

                assertFileExists(kotlinxDatetimePackageJson)
            }
        }
    }

    @DisplayName("Browser case works correctly with custom formatters")
    @GradleTest
    fun testWasmCustomFormattersUsage(gradleVersion: GradleVersion) {
        project("wasm-browser-simple-project", gradleVersion) {
            buildGradleKts.append(
                //language=Kotlin
                """
                |
                | tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile>().configureEach {
                |    compilerOptions.freeCompilerArgs.add("-Xwasm-debugger-custom-formatters")
                | }
                """.trimMargin()
            )

            build("wasmJsBrowserDistribution") {
                assertTasksExecuted(":compileKotlinWasmJs")
                assertTasksExecuted(":compileProductionExecutableKotlinWasmJs")
            }
        }
    }

    @DisplayName("Changed output module name")
    @GradleTest
    fun testChangedOutputModuleName(gradleVersion: GradleVersion) {
        project("wasm-browser-simple-project", gradleVersion) {
            val moduleName = "hello"
            buildGradleKts.modify {
                it.replace(
                    "wasmJs {",
                    """
                        wasmJs {
                            outputModuleName.set("$moduleName")
                    """.trimIndent()
                )
            }

            build("assemble") {
                assertFileExists(
                    projectPath.resolve("build/compileSync/wasmJs/main/productionExecutable/kotlin/$moduleName.mjs")
                )
            }
        }
    }

    // Android Studio touches all properties to analyze
    // It may break some properties with Task source
    // but we need to work in Android Studio,
    // so it is better to be sure that we work in this strange situation
    @DisplayName("Touch properties to not break Android studio")
    @GradleTest
    fun testTouchingWebpackPropertyToNotBreakAndroidStudio(gradleVersion: GradleVersion) {
        project("wasm-browser-simple-project", gradleVersion) {
            val moduleName = "hello"
            buildGradleKts.appendText(
                //language=kotlin
                """
                    
                    tasks.named<org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack>("wasmJsBrowserDevelopmentRun")                    
                        .get()
                        .inputFilesDirectory
                        .get()
                    
                """.trimIndent()
            )

            build("help")
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    @DisplayName("Different binaryen versions per project")
    @GradleTest
    fun testDifferentBinaryenVersions(gradleVersion: GradleVersion) {
        project("wasm-browser-several-modules", gradleVersion) {
            subProject("foo").let {
                it.buildScriptInjection {
                    project.extensions.getByType(BinaryenEnvSpec::class.java).version.set("119")
                }
            }

            subProject("bar").let {
                it.buildScriptInjection {
                    project.extensions.getByType(BinaryenEnvSpec::class.java).version.set("118")
                }
            }

            build(":foo:compileProductionExecutableKotlinWasmJsOptimize") {
                assertOutputContains(
                    Path("binaryen-version_119").resolve("bin").resolve("wasm-opt").pathString
                )
            }

            build(":bar:compileProductionExecutableKotlinWasmJsOptimize") {
                assertOutputContains(
                    Path("binaryen-version_118").resolve("bin").resolve("wasm-opt").pathString
                )
            }
        }
    }

    @DisplayName("Check js target test without kotlin-test")
    @GradleTest
    fun nodejsTestWithoutKotlinTest(gradleVersion: GradleVersion) {
        project("new-mpp-wasm-js", gradleVersion) {
            buildGradleKts.modify {
                it.replace(
                    "<JsEngine> {",
                    // filter is necessary to mute Gradle error
                    // https://docs.gradle.org/8.12.1/userguide/upgrading_version_8.html#test_task_fail_on_no_test_executed
                    """
                        |nodejs {
                        |    testTask {
                        |        filter.isFailOnNoMatchingTests = false
                        |        filter.includeTest("Foo", "bar")
                        |    }
                    """.trimMargin()
                )
            }

            projectPath.resolve("src/wasmJsTest/kotlin/foo.kt").apply {
                toFile().parentFile.mkdirs()
                writeText(
                    """
                    fun foo() = 73
                """.trimIndent()
                )
            }

            build("wasmJsTest") {
                assertTasksExecuted(":wasmJsTest")
            }
        }
    }

    @DisplayName("wasm js composite build works")
    @GradleTest
    fun testWasmJsCompositeBuild(gradleVersion: GradleVersion) {
        project(
            "wasm-composite-build",
            gradleVersion,
            // `:compileKotlinWasmJs` task is not compatible with CC on Gradle 7
            buildOptions = defaultBuildOptions.disableConfigurationCacheForGradle7(gradleVersion),
        ) {
            fun BuildResult.moduleVersion(rootModulePath: String, moduleName: String): String =
                projectPath.resolve(rootModulePath).toFile()
                    .resolve(NpmProject.PACKAGE_JSON)
                    .also {
                        if (!it.exists()) {
                            it
                                .parentFile // lib2
                                .parentFile // node_modules
                                .parentFile // js
                                .resolve(NpmProject.PACKAGE_JSON)
                                .let {
                                    println("root package.json:")
                                    println(it.readText())
                                }

                            it
                                .parentFile // lib2
                                .parentFile // node_modules
                                .parentFile // js
                                .resolve("packages_imported")
                                .also {
                                    println("ALL IMPORTED: ")
                                    it.listFiles()
                                        ?.forEach {
                                            println(it.absolutePath)
                                        }
                                    it.resolve(".visited-gradle").let {
                                        println("visited-gradle content")
                                        println(it.readText())
                                    }
                                }
                                .resolve("lib2")
                                .resolve("0.0.0-unspecified")
                                .resolve(NpmProject.PACKAGE_JSON)
                                .let {
                                    println("lib2 package.json state:")
                                    if (it.exists()) {
                                        println(it.readText())
                                    } else {
                                        println("lib2 package.json does not exists")
                                    }
                                }

                            printBuildOutput()
                        }
                    }
                    .let { fromSrcPackageJson(it) }
                    .let { it?.dependencies }
                    ?.getValue(moduleName)
                    ?: error("Not found package $moduleName in $rootModulePath")

            build("build") {
                val libDecamelizeVersion = moduleVersion("build/wasm/node_modules/lib-lib-2", "decamelize")
                assertEquals("1.1.1", libDecamelizeVersion)

                val libAsyncVersion = moduleVersion("build/wasm/node_modules/lib-lib-2", "async")
                assertEquals("2.6.2", libAsyncVersion)

                val appNodeFetchVersion = moduleVersion("build/wasm/node_modules/wasm-composite-build", "node-fetch")
                assertEquals("3.2.8", appNodeFetchVersion)
            }
        }
    }
}
