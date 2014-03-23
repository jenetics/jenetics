package org.jenetics.gradle.signing

import org.gradle.api.Project
import org.gradle.plugins.signing.SigningExtension
import org.gradle.plugins.signing.signatory.SignatoryProvider

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since @__version__@
 * @version @__version__@ &mdash; <em>$Date: 2014-03-23 $</em>
 */
class GPGSignatoryProvider implements SignatoryProvider<GPGSignatory> {
    @Override
    void configure(SigningExtension signingExtension, Closure closure) {
        println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC")
    }

    @Override
    GPGSignatory getDefaultSignatory(Project project) {
        println("DDDDDDDDDDDDDDDDDDDDDDDDD")
        return new GPGSignatory(project)
    }

    @Override
    GPGSignatory getSignatory(String s) {
        println("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF")
        return null
    }
}
