package org.jenetics.gradle.signing

import org.bouncycastle.openpgp.PGPSecretKey
import org.gradle.plugins.signing.signatory.pgp.PgpSignatory
import org.gradle.plugins.signing.signatory.pgp.PgpSignatoryFactory

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 2.0
 * @version 2.0 &mdash; <em>$Date$</em>
 */
class PGPSignatoryFactory extends PgpSignatoryFactory {

    @Override
    PgpSignatory createSignatory(String name, PGPSecretKey secretKey, String password) {
        new PGPSignatory(name, secretKey, password)
    }

}
