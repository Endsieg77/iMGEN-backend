package com.example.server.imgen.service;

public interface IPasswordService {
    byte[] getSalt(int length);

    byte[] getSalt32();
    
    String generateHash(String pwd, byte[] salt);
}
