package ru.track.server;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

public class SignatureUtils {

    public static void update(@NotNull Signature sig, byte[] buffer, int n) {
        try {
            sig.update(buffer, 0, n);
        } catch (SignatureException e) {
            throw new RuntimeException(e); // this never happens normally
        }
    }

    public static @NotNull Signature forSigning(@NotNull BigInteger secretExponent) {
        final Signature sig;
        try {
            sig = Signature.getInstance("SHA1withRSA");
            sig.initSign(getPrivateKey(secretExponent));
            return sig;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e); // this never happens normally
        }
    }

    public static byte[] sign(@NotNull Signature sig) {
        try {
            return sig.sign();
        } catch (SignatureException e) {
            throw new RuntimeException(e); // this never happens normally
        }
    }

    public static @NotNull Signature forChecking() {
        final Signature sig;
        try {
            sig = Signature.getInstance("SHA1withRSA");
            sig.initVerify(getPublicKey());
            return sig;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e); // this never happens normally
        }
    }

    public static boolean check(@NotNull Signature sig, byte[] signature) {
        try {
            return sig.verify(signature);
        } catch (SignatureException e) {
            throw new RuntimeException(e); // this never happens normally
        }
    }

    public static @NotNull PublicKey getPublicKey() {
        final KeySpec keySpec = new RSAPublicKeySpec(MODULUS, EXPONENT);
        try {
            return KeyFactory.getInstance("RSA").generatePublic(keySpec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e); // this never happens normally
        }
    }

    public static @NotNull PrivateKey getPrivateKey(BigInteger secretExponent) {
        final KeySpec keySpec = new RSAPrivateKeySpec(MODULUS, secretExponent);
        try {
            return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e); // this never happens normally
        }
    }

    public static final BigInteger MODULUS = new BigInteger("8a2fecd9d6bb588964aeb370bb787a175beaeefb561535a360172b74ae1c1011ba9213b5b8673468d0d9d82ab33fb51bcbf2ddaec704ff662bd68e146e3bcc2d", 0x10);
    public static final BigInteger EXPONENT = new BigInteger("64119b2b7b163a5a110d87a89024867495a1dadf7a8d1db5a22691e0029dc4699d820198a36a49208dec83a8e3ac5ddfcda7173819874ed45dc4b33b647b95a9", 0x10);

}
