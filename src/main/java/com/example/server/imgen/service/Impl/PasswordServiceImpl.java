package com.example.server.imgen.service.Impl;

import java.security.SecureRandom;
import java.util.Random;
import org.springframework.stereotype.Service;
import com.example.server.imgen.service.IPasswordService;
import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

@Service
public class PasswordServiceImpl implements IPasswordService {
    @Override
    public byte[] getSalt(int length)
    {
        final Random r = new SecureRandom();
        byte[] salt = new byte[length];
        r.nextBytes(salt);
    
        return salt;
    }

    @Override
    public byte[] getSalt32()
    {
        return getSalt(32);
    }
    
    @Override
    public String generateHash(String pwd, byte[] salt)
    {
        var saltString = new String(salt);
        var all = pwd.concat(saltString);

        HashCode sha256 = Hashing.sha256()
                            .hashString(all, Charsets.UTF_8);

        return Integer.toHexString(sha256.asInt());
    }
}
