/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.targets.js.npm

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.TaskProvider
import org.gradle.process.ExecOperations
import org.jetbrains.kotlin.gradle.internal.ConfigurationPhaseAware
import org.jetbrains.kotlin.gradle.logging.kotlinInfo
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnv
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NpmApiExtension
import org.jetbrains.kotlin.gradle.targets.web.nodejs.BaseNodeJsRootExtension

/**
 * Abstract base class for configuring and managing NPM-related tasks and environment within a Kotlin/Gradle project.
 * Designed to be used in conjunction with Node.js tooling for managing dependencies, scripts, and project configurations.
 *
 * @property project The root Gradle project associated with this extension.
 * @property nodeJsRoot The root Node.js extension providing configuration and context for managing Node.js tooling.
 */
abstract class BaseNpmExtension internal constructor(
    val project: Project,
    val nodeJsRoot: BaseNodeJsRootExtension,
    private val objects: ObjectFactory,
    private val execOps: ExecOperations,
) : ConfigurationPhaseAware<NpmEnv>(), NpmApiExtension<NpmEnvironment, Npm> {
    init {
        check(project == project.rootProject)
        project.logger.kotlinInfo("Storing cached files in ${project.gradle.gradleUserHomeDir}")
    }

    override val packageManager: Npm by lazy {
        Npm(
            objects = objects,
            execOps = execOps,
        )
    }

    override val environment: NpmEnvironment by lazy {
        requireConfigured().asNpmEnvironment
    }

    override val additionalInstallOutput: FileCollection = project.objects.fileCollection().from(
        nodeJsRoot.rootPackageDirectory.map { it.file("package-lock.json") }
    )

    override val preInstallTasks: ListProperty<TaskProvider<*>> = project.objects.listProperty(TaskProvider::class.java)

    override val postInstallTasks: ListProperty<TaskProvider<*>> = project.objects.listProperty(TaskProvider::class.java)

    val command: org.gradle.api.provider.Property<String> = project.objects.property(String::class.java)
        .convention("npm")

    val lockFileName: org.gradle.api.provider.Property<String> = project.objects.property(String::class.java)
        .convention(LockCopyTask.PACKAGE_LOCK)
    val lockFileDirectory: DirectoryProperty = project.objects.directoryProperty()
        .convention(project.layout.projectDirectory.dir(LockCopyTask.KOTLIN_JS_STORE))

    val ignoreScripts: org.gradle.api.provider.Property<Boolean> = project.objects.property(Boolean::class.java)
        .convention(true)

    val packageLockMismatchReport: org.gradle.api.provider.Property<LockFileMismatchReport> =
        project.objects.property(LockFileMismatchReport::class.java)
            .convention(LockFileMismatchReport.FAIL)

    val reportNewPackageLock: org.gradle.api.provider.Property<Boolean> = project.objects.property(Boolean::class.java)
        .convention(false)

    val packageLockAutoReplace: org.gradle.api.provider.Property<Boolean> = project.objects.property(Boolean::class.java)
        .convention(false)

    val overrides: ListProperty<NpmOverride> = project.objects.listProperty(NpmOverride::class.java)

    fun override(path: String, configure: Action<NpmOverride>) {
        overrides.add(
            NpmOverride(path)
                .apply { configure.execute(this) }
        )
    }

    fun override(path: String, version: String) {
        override(path, Action {
            it.include(version)
        })
    }

    internal val nodeJsEnvironment: org.gradle.api.provider.Property<NodeJsEnv> = project.objects.property(NodeJsEnv::class.java)

    override fun finalizeConfiguration(): NpmEnv {
        val nodeJsEnvironmentValue = nodeJsEnvironment.get()
        val isWindows = nodeJsEnvironmentValue.isWindows

        fun getExecutable(command: String, customCommand: String, windowsExtension: String): String {
            val finalCommand = if (isWindows && customCommand == command) "$command.$windowsExtension" else customCommand
            return if (nodeJsEnvironmentValue.download)
                nodeJsEnvironmentValue.nodeBinDir
                    .resolve(finalCommand).absolutePath
            else
                finalCommand
        }
        return NpmEnv(
            executable = getExecutable("npm", command.get(), "cmd"),
            ignoreScripts = ignoreScripts.get(),
            standalone = !nodeJsEnvironmentValue.download,
            packageLockMismatchReport = packageLockMismatchReport.get(),
            reportNewPackageLock = reportNewPackageLock.get(),
            packageLockAutoReplace = packageLockAutoReplace.get(),
            overrides = overrides.get()
        )
    }

    val restorePackageLockTaskProvider: TaskProvider<out Task>
        get() = project.tasks.named(nodeJsRoot.extensionName(LockCopyTask.RESTORE_PACKAGE_LOCK_BASE_NAME))

    val storePackageLockTaskProvider: TaskProvider<out Task>
        get() = project.tasks.named(nodeJsRoot.extensionName(LockCopyTask.STORE_PACKAGE_LOCK_BASE_NAME))
}
