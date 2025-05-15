package de.hitec.nhplus.utils;

public class PhoneNumberUtil {

    public static boolean isValidPhoneNumber(String phoneNumber) {
        // Check if the phone number contains only digits and has a valid length
        return phoneNumber.matches("^\\+\\d{1,3} \\d{2,13} \\d+x?\\d{0,20}$");
    }
}
