package com.SecureLibrarySystem.webapp.crypto;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class AESEncryptConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return attribute == null ? null : AESUtil.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return AESUtil.decrypt(dbData);
        } catch (Exception e) {
            // If decryption fails, return the data as-is (likely plain text from old records)
            return dbData;
        }
    }
}
