package org.jenetics.gradle.signing

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openpgp.PGPSecretKey
import org.bouncycastle.openpgp.PGPSignature
import org.bouncycastle.openpgp.PGPSignatureGenerator
import org.bouncycastle.openpgp.PGPUtil
import org.gradle.plugins.signing.signatory.pgp.PgpSignatory

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 2.0
 * @version 2.0 &mdash; <em>$Date: 2014-03-23 $</em>
 */
class PGPSignatory extends PgpSignatory {

    PGPSignatory(String name, PGPSecretKey secretKey, String password) {
        super(name, secretKey, password)
    }

    @Override
    PGPSignatureGenerator createSignatureGenerator() {
        def generator = new PGPSignatureGenerator(
            secretKey.publicKey.algorithm,
            PGPUtil.SHA256,
            BouncyCastleProvider.PROVIDER_NAME
        )
        generator.initSign(PGPSignature.BINARY_DOCUMENT, privateKey)
        generator
    }

}
