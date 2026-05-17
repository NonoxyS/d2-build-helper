package tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class RenameOutputTask : DefaultTask() {
    @get:InputFile
    abstract val inputFile: RegularFileProperty

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun rename() {
        val input = inputFile.asFile.get()
        val output = outputFile.asFile.get()

        output.parentFile.mkdirs()
        input.copyTo(output, overwrite = true)
    }
}
