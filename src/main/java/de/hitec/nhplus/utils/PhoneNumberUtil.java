package de.hitec.nhplus.utils;

/**
 * Utility class to validate phone numbers.
 */
public class PhoneNumberUtil {

    /**
     * Checks if a phone number is valid using a regular expression.
     *
     * Phone numbers must be in this format: <code>+49 176 12345678</code>
     *
     * @param phoneNumber The phone number to check.
     * @return <code>true</code> if the phone number is valid, <code>false</code> otherwise.
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        // Check if the phone number contains only digits and has a valid length
        return phoneNumber.matches("^\\+\\d{1,3} \\d{2,13} \\d+x?\\d{0,20}$");
    }
}
