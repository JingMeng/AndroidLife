package com.camnter.gradle.plugin.reduce.dependency.packaging

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.api.ApplicationVariant
import com.camnter.gradle.plugin.reduce.dependency.packaging.hooker.TaskHookerManager
import com.camnter.gradle.plugin.reduce.dependency.packaging.transform.ReduceDependencyPackagingTransform
import com.camnter.gradle.plugin.reduce.dependency.packaging.utils.FileBinaryCategory
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.internal.reflect.Instantiator
import org.gradle.tooling.provider.model.ToolingModelBuilderRegistry

import javax.inject.Inject

/**
 * Refer from VirtualAPK
 *
 * @author CaMnter
 */

class ReduceDependencyPackagingPlugin implements Plugin<Project> {

    private Project project
    private Instantiator instantiator
    private ReduceDependencyPackagingExtension reduceDependencyPackagingExtension

    /**
     * Stores files generated by the host side and is used when building plugin apk
     * ${project.rootDir}/host
     * */
    private File hostDir

    /**
     * TaskHooker manager, registers hookers when apply invoked
     * */
    private TaskHookerManager taskHookerManager

    @Inject
    public ReduceDependencyPackagingPlugin(Instantiator instantiator,
            ToolingModelBuilderRegistry registry) {
        this.instantiator = instantiator
    }

    @Override
    void apply(Project project) {
        this.project = project
        final ExtensionContainer extensions = project.extensions
        if (!project.plugins.hasPlugin(AppPlugin.class)) {
            println "[ReduceDependencyPackagingPlugin]   reduce-dependency-packaging-plugin requires the Android plugin to be configured"
            return
        }

        taskHookerManager = new TaskHookerManager(project, instantiator)
        taskHookerManager.registerTaskHookers()

        project.extensions.create('reduceDependencyPackagingExtension',
                ReduceDependencyPackagingExtension)
        final AppExtension android = extensions.getByType(AppExtension.class)

        initExtension(project, android)
        android.registerTransform(
                new ReduceDependencyPackagingTransform(project, android.applicationVariants))
    }

    def initExtension(Project project, AppExtension android) {
        hostDir = project.file('host')
        if (!hostDir.exists()) {
            hostDir.mkdirs()
        }
        reduceDependencyPackagingExtension = project.reduceDependencyPackagingExtension
        project.afterEvaluate {
            android.applicationVariants.each { ApplicationVariant variant ->
                checkConfig()
                reduceDependencyPackagingExtension.with {
                    packageName = getApplicationId(project, variant)
                    packagePath = packageName.replace('.'.charAt(0), File.separatorChar)
                    hostSymbolFile = new File(hostDir, "Host_R.txt")
                    hostDependenceFile = new File(hostDir, "versions.txt")
                }
            }
        }
    }

    /**
     * Check the plugin apk related config info
     * */
    private void checkConfig() {
        final int packageId = reduceDependencyPackagingExtension.packageId
        if (packageId == 0) {
            def err = new StringBuilder('you should set the packageId in build.gradle,\n ')
            err.append('please declare it in application project build.gradle:\n')
            err.append('    reduceDependencyPackagingExtension {\n')
            err.append('        packageId = 0xXX \n')
            err.append('    }\n')
            err.append(
                    'apply for the value of packageId\n')
            throw new InvalidUserDataException(err.toString())
        }

        final String targetHost = reduceDependencyPackagingExtension.targetHost
        if (!targetHost) {
            def err = new StringBuilder(
                    '\nyou should specify the targetHost in build.gradle, e.g.: \n')
            err.append('    reduceDependencyPackagingExtension {\n')
            err.append(
                    '        //when target Host in local machine, value is host application directory\n')
            err.append('        targetHost = ../xxxProject/app \n')
            err.append('    }\n')
            throw new InvalidUserDataException(err.toString())
        }

        final File hostLocalDir = project.file(targetHost)
        if (!hostLocalDir.exists()) {
            def err = "The directory of host application doesn't exist! Dir: ${hostLocalDir.absolutePath}"
            throw new InvalidUserDataException(err)
        }

        final File hostR = new File(hostLocalDir, "build/reduceDependencyPackagingHost/Host_R.txt")
        if (hostR.exists()) {
            def dst = new File(hostDir, "Host_R.txt")
            use(FileBinaryCategory) {
                dst << hostR
            }
        } else {
            def err = new StringBuilder(
                    "Can't find ${hostR.path}, please check up your host application\n")
            err.append(
                    "  need apply com.camnter.gradle.plugin.reduce.dependency.packaging.host in build.gradle of host application\n ")
            throw new InvalidUserDataException(err.toString())
        }

        final File hostVersions = new File(hostLocalDir, "build/reduceDependencyPackagingHost/versions.txt")
        if (hostVersions.exists()) {
            def dst = new File(hostDir, "versions.txt")
            use(FileBinaryCategory) {
                dst << hostVersions
            }
        } else {
            def err = new StringBuilder(
                    "Can't find ${hostVersions.path}, please check up your host application\n")
            err.append(
                    "  need apply com.camnter.gradle.plugin.reduce.dependency.packaging.host in build.gradle of host application \n")
            throw new InvalidUserDataException(err.toString())
        }

        final File hostMapping = new File(hostLocalDir, "build/reduceDependencyPackagingHost/mapping.txt")
        if (hostMapping.exists()) {
            def dst = new File(hostDir, "mapping.txt")
            use(FileBinaryCategory) {
                dst << hostMapping
            }
        }
    }
}