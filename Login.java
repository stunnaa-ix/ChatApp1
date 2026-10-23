package com.chatapp;

import java.util.regex.Pattern;

/**
 * Handles account registration and login for Part 1 of the Chat App PoE.
 *
 * Design notes (read before you record your presentation video, since you
 * need to be able to explain these choices):
 *
 * 1. The task's registration step only lists username, password and cell
 *    number as inputs, but the login success message needs a first name and
 *    last name ("Welcome <first name>, <last name> ..."). Those two fields
 *    are therefore captured at registration time too, even though they
 *    aren't validated against any rule (the brief doesn't give one).
 *
 * 2. registerUser() validates all three required fields (username,
 *    password, cell number) and returns the message for whichever one
 *    fails first. This class only "remembers" one registered user at a
 *    time, which is enough for Part 1 - the login for a chat app doesn't
 *    need multi-user storage until later parts introduce persistence.
 *
 * Regex references (per the task's "attribute this code" requirement):
 * - Password complexity lookahead pattern: common approach described in
 *   Baeldung, "Java Regex Password Validation" (baeldung.com).
 * - International phone number formatting: general guidance from the
 *   article linked in the brief, quickblox.com/blog/beginners-guide-to-chat-app-architecture/
 */
public class Login {

    // Username: must contain an underscore and be no more than 5 characters long
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^(?=.*_).{1,5}$");

    // Password: at least 8 characters, 1 capital letter, 1 digit, 1 special character
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Z])(?=.*[0-9])(?=.*[^a-zA-Z0-9]).{8,}$");

    // Cell number: South African international code (+27) followed by up to 10 digits
    private static final Pattern CELL_PATTERN = Pattern.compile("^\\+27[0-9]{1,10}$");

    // The single registered user for this part of the PoE
    private String registeredFirstName;
    private String registeredLastName;
    private String registeredUsername;
    private String registeredPassword;
    private String registeredCellPhoneNumber;

    public boolean checkUserName(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }

    public boolean checkPasswordComplexity(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    public boolean checkCellPhoneNumber(String cellPhoneNumber) {
        return cellPhoneNumber != null && CELL_PATTERN.matcher(cellPhoneNumber).matches();
    }

    /**
     * Validates and, if everything passes, stores the new user's details.
     *
     * @return the exact system message for the first rule that fails,
     *         or a success message if every rule passes.
     */
    public String registerUser(String firstName, String lastName, String username,
                                String password, String cellPhoneNumber) {

        if (!checkUserName(username)) {
            return "Username is not correctly formatted; please ensure that your "
                    + "username contains an underscore and is no more than five "
                    + "characters in length.";
        }

        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted; please ensure that the "
                    + "password contains at least eight characters, a capital "
                    + "letter, a number, and a special character.";
        }

        if (!checkCellPhoneNumber(cellPhoneNumber)) {
            return "Cell number is incorrectly formatted or does not contain an "
                    + "international code; please correct the number and try again.";
        }

        this.registeredFirstName = firstName;
        this.registeredLastName = lastName;
        this.registeredUsername = username;
        this.registeredPassword = password;
        this.registeredCellPhoneNumber = cellPhoneNumber;

        return "Username successfully captured. Password successfully captured. "
                + "Cell number successfully captured. Registration successful.";
    }

    public boolean loginUser(String username, String password) {
        return registeredUsername != null
                && registeredUsername.equals(username)
                && registeredPassword.equals(password);
    }

    public String returnLoginStatus(boolean loginSuccessful) {
        if (loginSuccessful) {
            return "Welcome " + registeredFirstName + ", " + registeredLastName
                    + " it is great to see you again.";
        }
        return "Username or password incorrect, please try again.";
    }
}
