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
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 2.0
 * @version 2.0
 */
class FixJavadocCodeBlockTask extends DefaultTask {

    @InputDirectory
    public File directory

    @Input
    @Optional
    public String extensions = '.java'

    @TaskAction
    public void fix() {
        int fileCount = 0
        int trimmedFileCount = 0

        final String[] patterns = extensions.split(':')
        directory.eachDirRecurse { dir ->
            dir.eachFile { file ->
                if (matches(patterns, file.name)) {
                    fileCount += 1

                    boolean fixed = fixFile(file)
                    if (fixed) {
                        trimmedFileCount += 1
                        getLogger().info("Fixed file '$file'")
                    }
                }
            }
        }

        getLogger().lifecycle("$trimmedFileCount of $fileCount files fixed.")
    }

    private static def fixFile(final File file) {
        final String text = file.text

        final StringBuilder out = new StringBuilder(10000);
        State state = State.DATA;
        boolean modified = false;

        for (int i = 0; i < text.length(); ++i) {
            final char ch = text.charAt(i)
            if (state == State.CODE_TAG) {
                switch (ch) {
                    case '<': out.append("&lt;"); modified = true; break;
                    case '>': out.append("&gt;"); modified = true; break;
                    default: out.append(ch); break;
                }
            } else {
                out.append(ch);
            }
            state = state.apply(ch, out);
        }

        if (modified) {
            file.write(out.toString())
        }

        modified
    }

    static enum State {
        DATA {
            @Override
            State apply(final char ch, final StringBuilder out) {
                State state = this;
                if ((ch == '*') &&
                        (out.length() > 2) &&
                        out.substring(out.length() - 3).equalsIgnoreCase("/**"))
                {
                    state = JAVADOC;
                }

                return state;
            }
        },
        JAVADOC {
            @Override
            State apply(final char ch, final StringBuilder out) {
                State state = this;
                if ((ch == ']') &&
                    (out.length() > 5) &&
                    out.substring(out.length() - 6).equalsIgnoreCase("[code]"))
                {
                    state = CODE_TAG;
                } else if ((ch == '/') &&
                        (out.length() > 1) &&
                        out.substring(out.length() - 2).equalsIgnoreCase("*/"))
                {
                    state = DATA;
                }

                return state;
            }
        },
        CODE_TAG {
            @Override
            State apply(final char ch, final StringBuilder out) {
                State state = this;
                if ((ch == ']') &&
                    out.substring(out.length() - 7).equalsIgnoreCase("[/code]"))
                {
                    state = JAVADOC;
                }

                return state;
            }
        };

        State apply(final char ch, final StringBuilder out) {
            println(ch)
        }
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
