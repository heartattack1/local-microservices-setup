package com.example.demo2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    // Извлекаем секреты из Vault по ключам
    @Value("${api_key}")
    private String apiKey;

    @Value("${api_secret}")
    private String apiSecret;

    @GetMapping("/vault-api-keys")
    public String getVaultApiKeys() {
        return "API Key: " + apiKey + ", API Secret: " + apiSecret;
    }
}
