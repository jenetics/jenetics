package org.jenetics.gradle.signing

import org.gradle.api.Project
import org.gradle.plugins.signing.SigningExtension
import org.gradle.plugins.signing.signatory.pgp.Dsl
import org.gradle.plugins.signing.signatory.pgp.PgpSignatory
import org.gradle.plugins.signing.signatory.pgp.PgpSignatoryProvider
import org.gradle.util.ConfigureUtil

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 2.0
 * @version 2.0 &mdash; <em>$Date: 2014-03-23 $</em>
 */
class PGPSignatoryProvider extends PgpSignatoryProvider {
    private final factory = new PGPSignatoryFactory()
    private final Map<String, PgpSignatory> signatories = [:]

    void configure(SigningExtension settings, Closure closure) {
        ConfigureUtil.configure(closure, new Dsl(settings.project, signatories, factory))
    }

    PgpSignatory getDefaultSignatory(Project project) {
        factory.createSignatory(project)
    }

    PgpSignatory getSignatory(String name) {
        signatories[name]
    }

    PgpSignatory propertyMissing(String signatoryName) {
        getSignatory(signatoryName)
    }
}
