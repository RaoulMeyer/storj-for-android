package io.storj.android.crypto;

import org.spongycastle.jcajce.provider.asymmetric.ec.KeyPairGeneratorSpi;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;

public class ECDSA {

    public KeyPair newKeyPair() {
        try {
            final KeyPairGeneratorSpi.ECDSA ecdsa = new KeyPairGeneratorSpi.ECDSA();

            ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");
            ecdsa.initialize(ecSpec, new SecureRandom());

            return ecdsa.generateKeyPair();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return null;
    }

}
