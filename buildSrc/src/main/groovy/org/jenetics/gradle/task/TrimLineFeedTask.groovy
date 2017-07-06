/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.gradle.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 * Removes all leading and trailing new lines from a file except one trailing
 * new line at the end.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.6
 * @version 1.6
 */
class TrimLineFeedTask extends DefaultTask {

    private static String LINE_SEPARATOR = System.getProperty("line.separator")

    @InputDirectory
    public File directory

    @Input
    @Optional
    public String extensions = '.java:.groovy'

    @TaskAction
    public void trim() {
        int fileCount = 0
        int trimmedFileCount = 0

        final String[] patterns = extensions.split(':')
        directory.eachDirRecurse { dir ->
            dir.eachFile { file ->
                if (matches(patterns, file.name)) {
                    fileCount += 1

                    boolean trimmed = trimFile(file)
                    if (trimmed) {
                        trimmedFileCount += 1
                        getLogger().info("Trimmed file '$file'")
                    }
                }
            }
        }

        getLogger().lifecycle("$trimmedFileCount of $fileCount files trimmed.")
    }

    private static def trimFile(final File file) {
        final String text = file.text
        final String trimmedText = text.trim() + LINE_SEPARATOR

        final boolean equal = text.equals(trimmedText)
        if (!equal) {
            file.write(trimmedText)
        }

        !equal
    }

    private static def matches(final String[] patterns, final String name) {
        for (int i = 0; i < patterns.length; ++i) {
            if (name.endsWith(patterns[i])) {
                return true
            }
        }

        return false
    }

}
