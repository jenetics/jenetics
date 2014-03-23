package org.jenetics.gradle.signing

import org.gradle.api.Project
import org.gradle.plugins.signing.signatory.Signatory
import org.gradle.plugins.signing.SignOperation

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 2.0
 * @version 2.0 &mdash; <em>$Date: 2014-03-23 $</em>
 */
class GPGSignatory implements Signatory {

    private Project project

    GPGSignatory(final Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return "GPG"
    }

    @Override
    void sign(final InputStream inputStream, final OutputStream outputStream) {
        outputStream.write(sign(inputStream))
    }

   //gpg -stab LICENSE.txt

    @Override
    byte[] sign(final InputStream inputStream) {
        final File tempFile = File.createTempFile("__gradle__", "__gpg__" )
        tempFile.deleteOnExit()

        tempFile.withOutputStream { out ->
            out.write(inputStream.bytes)
        }

        project.exec { exe ->
            exe.commandLine 'gpg', '-sta', tempFile.absolutePath
        }.rethrowFailure()

        return new File(tempFile + '.asc').bytes
    }

}
