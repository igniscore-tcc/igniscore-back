package com.igniscore.api.utils;

public class CnpjValidator {

    private CnpjValidator() {}

    public static boolean isValid(String cnpj) {

        if (cnpj == null) {
            return false;
        }

        cnpj = cnpj.replaceAll("[^\\d]", "");

        if (cnpj.length() != 14) {
            return false;
        }

        if (cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }

        try {

            int[] weightFirstDigit = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int[] weightSecondDigit = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

            int sum = 0;

            for (int i = 0; i < 12; i++) {
                sum += (cnpj.charAt(i) - '0') * weightFirstDigit[i];
            }

            int remainder = sum % 11;
            int firstDigit = remainder < 2 ? 0 : 11 - remainder;

            if ((cnpj.charAt(12) - '0') != firstDigit) {
                return false;
            }

            sum = 0;

            for (int i = 0; i < 13; i++) {
                sum += (cnpj.charAt(i) - '0') * weightSecondDigit[i];
            }

            remainder = sum % 11;
            int secondDigit = remainder < 2 ? 0 : 11 - remainder;

            return (cnpj.charAt(13) - '0') == secondDigit;

        } catch (Exception e) {
            return false;
        }
    }

    public static String normalize(String cnpj) {
        if (cnpj == null) {
            return null;
        }

        return cnpj.replaceAll("[^\\d]", "");
    }
}