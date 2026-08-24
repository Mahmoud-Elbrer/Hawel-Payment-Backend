package com.hawel.transaction_service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hawel.transaction_service.dto.request.TransferTransactionRequest;
import com.hawel.transaction_service.exception.TransactionException;
import com.hawel.transaction_service.service.RequestHashService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class RequestHashServiceImpl implements RequestHashService {

    private final ObjectMapper objectMapper;

    @Override
    public String generateHash(TransferTransactionRequest request) {

        try {
            String json = objectMapper.writeValueAsString(request);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(json.getBytes(StandardCharsets.UTF_8));

            return bytesToHex(hash);

        } catch (JsonProcessingException e) {
            throw new TransactionException("Failed to serialize transaction request" + e);

        } catch (NoSuchAlgorithmException e) {
            throw new TransactionException("SHA-256 algorithm not available" + e);
        }
    }

    private String bytesToHex(byte[] bytes) {

        StringBuilder result = new StringBuilder();

        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }

        return result.toString();
    }
}