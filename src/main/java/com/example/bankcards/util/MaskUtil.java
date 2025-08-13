package com.example.bankcards.util;

public class MaskUtil {

    public static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 8) {
            return "****";
        }
        String start = cardNumber.substring(0, 4);
        String end = cardNumber.substring(cardNumber.length() - 4);
        return start + " **** **** " + end;
    }

    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***";
        }
        int atIndex = email.indexOf("@");
        String name = email.substring(0, atIndex);
        if (name.length() <= 2) {
            return "***" + email.substring(atIndex);
        }
        return name.charAt(0) + "***" + name.charAt(name.length() - 1) + email.substring(atIndex);
    }
}
