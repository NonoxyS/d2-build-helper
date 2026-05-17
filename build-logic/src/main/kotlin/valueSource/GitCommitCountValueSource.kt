package valueSource

import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

abstract class GitCommitCountValueSource : ValueSource<Int, ValueSourceParameters.None> {

    @get:Inject
    abstract val execOperations: ExecOperations

    override fun obtain(): Int {
        val output = ByteArrayOutputStream()

        return try {
            execOperations.exec {
                commandLine("git", "rev-list", "--count", "HEAD")
                standardOutput = output
            }.rethrowFailure()

            output.toString().trim().toInt()
        } catch (throwable: Throwable) {
            throw IllegalStateException(
                "Error occur during getting Git commit count",
                throwable,
            )
        }
    }
}
