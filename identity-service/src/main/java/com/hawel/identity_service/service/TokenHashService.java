package com.hawel.identity_service.service;

public interface TokenHashService   {
    String hash(String token);
}
