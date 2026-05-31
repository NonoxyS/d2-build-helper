package valueSource

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

abstract class GitCommitCountValueSource : ValueSource<Int, GitCommitCountValueSource.Params> {

    interface Params : ValueSourceParameters {
        val workingDir: DirectoryProperty
    }

    @get:Inject
    abstract val execOperations: ExecOperations

    override fun obtain(): Int {
        val stdout = ByteArrayOutputStream()
        val stderr = ByteArrayOutputStream()

        return try {
            execOperations.exec {
                workingDir = parameters.workingDir.get().asFile
                commandLine("git", "rev-list", "--count", "HEAD")
                standardOutput = stdout
                errorOutput = stderr
            }.rethrowFailure()

            stdout.toString().trim().toInt()
        } catch (throwable: Throwable) {
            val details = stderr.toString().trim().ifBlank { throwable.message.orEmpty() }
            throw IllegalStateException(
                "Error obtaining git commit count: $details",
                throwable,
            )
        }
    }
}
