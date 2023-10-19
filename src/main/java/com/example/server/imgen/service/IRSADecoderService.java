package com.example.server.imgen.service;

public interface IRSADecoderService {

    String decode(String base64PrivateKey, String base64Encoded) throws Exception;
    
}
