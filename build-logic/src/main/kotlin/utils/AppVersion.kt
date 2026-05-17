@file:Suppress("Filename")

package utils

import extensions.libs
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import valueSource.GitCommitCountValueSource

object AppVersion {

    /** Returns version name in format "$majorVersion.$minorVersion.{commitCount}" */
    fun getVersionName(project: Project): Provider<String> {
        val majorVersion = project.libs.versions.appVersion.major.get()
        val minorVersion = project.libs.versions.appVersion.minor.get()

        return getVersionCode(project).map { count -> "$majorVersion.$minorVersion.$count" }
    }

    /** Returns version code equal to the number of Git commits */
    fun getVersionCode(project: Project): Provider<Int> {
        return project.providers.of(GitCommitCountValueSource::class.java) {}
    }
}
