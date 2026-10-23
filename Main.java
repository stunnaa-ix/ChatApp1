package com.chatapp;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Login login = new Login();

        System.out.println("=== Chat App: Registration ===");

        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();

        System.out.print("Enter username (must contain '_' and be no more than 5 characters): ");
        String username = scanner.nextLine();

        System.out.print("Enter password (min 8 chars, 1 capital letter, 1 number, 1 special character): ");
        String password = scanner.nextLine();

        System.out.print("Enter South African cell number (e.g. +27838968976): ");
        String cellPhoneNumber = scanner.nextLine();

        String registrationResult = login.registerUser(firstName, lastName, username, password, cellPhoneNumber);
        System.out.println();
        System.out.println(registrationResult);

        if (!registrationResult.contains("Registration successful")) {
            scanner.close();
            return;
        }

        System.out.println();
        System.out.println("=== Chat App: Login ===");

        System.out.print("Enter username: ");
        String loginUsername = scanner.nextLine();

        System.out.print("Enter password: ");
        String loginPassword = scanner.nextLine();

        boolean loginSuccessful = login.loginUser(loginUsername, loginPassword);
        System.out.println();
        System.out.println(login.returnLoginStatus(loginSuccessful));

        // Requirement: users may only send messages once logged in successfully
        if (!loginSuccessful) {
            scanner.close();
            return;
        }

        System.out.println();
        System.out.println("Welcome to QuickChat.");

        System.out.print("How many messages would you like to send this session? ");
        int messageLimit = Integer.parseInt(scanner.nextLine().trim());
        int messagesComposed = 0;

        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    if (messagesComposed >= messageLimit) {
                        System.out.println("You've reached your limit of " + messageLimit
                                + " message(s) for this session.");
                        break;
                    }
                    composeMessage(scanner, messagesComposed);
                    messagesComposed++;
                    break;

                case "2":
                    System.out.println("Coming Soon.");
                    break;

                case "3":
                    System.out.println();
                    System.out.println("Total messages sent: " + Message.returnTotalMessages());
                    System.out.println(Message.printMessages());
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option, please choose 1, 2 or 3.");
            }
        }

        scanner.close();
    }

    private static void composeMessage(Scanner scanner, int messageNumber) {
        System.out.print("Enter recipient cell number (e.g. +27718693002): ");
        String recipient = scanner.nextLine().trim();

        System.out.print("Enter your message (no more than 250 characters): ");
        String text = scanner.nextLine();

        Message message = new Message(recipient, text, messageNumber);

        System.out.println(message.messageIdConfirmation());

        String recipientCheck = message.checkRecipientCell();
        System.out.println(recipientCheck);
        if (!recipientCheck.startsWith("Cell phone number successfully")) {
            return;
        }

        String lengthCheck = message.checkMessageLength();
        System.out.println(lengthCheck);
        if (!lengthCheck.equals("Message ready to send.")) {
            return;
        }

        message.createMessageHash();

        System.out.println();
        System.out.println("Choose an action for this message:");
        System.out.println("1) Send Message");
        System.out.println("2) Disregard Message");
        System.out.println("3) Store Message to send later");
        System.out.print("Choose an option: ");
        String actionChoice = scanner.nextLine().trim();

        String action;
        switch (actionChoice) {
            case "1": action = "send"; break;
            case "2": action = "disregard"; break;
            case "3": action = "store"; break;
            default:
                System.out.println("Invalid option selected.");
                return;
        }

        String result = message.sendMessage(action);
        System.out.println(result);

        if (action.equals("send")) {
            System.out.println();
            System.out.println(message.fullDetails());
        }
    }
}
