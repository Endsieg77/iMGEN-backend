package com.example.server.imgen.service.Impl;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import javax.crypto.Cipher;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;

import com.example.server.imgen.service.IRSADecoderService;
import com.google.common.base.Charsets;

@Service
public class RSADecoderImpl implements IRSADecoderService {
    @Override
    public String decode(String base64PrivateKey, String base64Encoded) throws Exception
    {
        base64PrivateKey = base64PrivateKey
            .replace("-----BEGIN PRIVATE KEY----", "")
            .replace("-----END PRIVATE KEY----", "")
            .replace("\n", "");
        var privateKeyBytes         = Base64.decodeBase64(base64PrivateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory kf               = KeyFactory.getInstance("RSA");
        PrivateKey privateKey       = kf.generatePrivate(keySpec);
        Cipher dCipher              = Cipher.getInstance("RSA");
        dCipher.init(Cipher.DECRYPT_MODE, privateKey);

        return new String(dCipher.doFinal(Base64.decodeBase64(base64Encoded)), Charsets.UTF_8);
    }
}
