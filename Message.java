package com.chatapp;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Represents a single chat message for Part 2 (Sending Messages).
 *
 * Design notes (read before your video):
 *
 * 1. "Num messages sent" (a per-message field in the brief) and the
 *    "number of the message" used inside the message hash are treated as
 *    the same thing here: a zero-based count of how many messages have
 *    been composed so far in this session. Message 1 -> 0, message 2 -> 1,
 *    and so on - that's what makes the sample hash "00:0:HITONIGHT" work
 *    out to messageNumber 0 for the first message.
 *
 * 2. createMessageHash() uses this object's own messageID, so the "00"
 *    prefix in the brief's example depends on whatever ID happens to be
 *    generated. For the unit test that checks the hash is built
 *    correctly, there's a second constructor below that accepts a fixed
 *    message ID, so the test can supply the exact ID from the brief and
 *    get a reproducible hash. Normal use (from Main) always uses the
 *    random-ID constructor.
 *
 * 3. checkRecipientCell() reuses the same South African cell-number
 *    pattern from Login.checkCellPhoneNumber() in Part 1 (per the brief's
 *    hint to reuse that method/tests), but returns a message string
 *    instead of a boolean, since that's what this part's method table and
 *    unit tests expect.
 *
 * 4. The message-length check isn't named in the Method Name table, but
 *    the brief's unit test table requires it, so it's added here as
 *    checkMessageLength().
 */
public class Message {

    private static final Pattern CELL_PATTERN = Pattern.compile("^\\+27[0-9]{1,10}$");
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Path JSON_FILE = Path.of("messages.json");

    // Running totals shared across all Message instances in this session
    private static int totalMessagesSent = 0;
    private static final List<Message> sentMessages = new ArrayList<>();

    private final String messageID;
    private final int messageNumber;
    private String recipient;
    private String messageText;
    private String messageHash;

    /** Normal use: generates a fresh random 10-digit message ID. */
    public Message(String recipient, String messageText, int messageNumber) {
        this(generateMessageID(), recipient, messageText, messageNumber);
    }

    /** Testing use: lets a unit test supply a fixed message ID. */
    public Message(String messageID, String recipient, String messageText, int messageNumber) {
        this.messageID = messageID;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageNumber = messageNumber;
    }

    private static String generateMessageID() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    /** Matches the unit test's "Message ID is created" expected output. */
    public String messageIdConfirmation() {
        return "Message ID generated: " + messageID;
    }

    public String checkRecipientCell() {
        boolean valid = recipient != null && CELL_PATTERN.matcher(recipient).matches();
        if (valid) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.";
    }

    public String checkMessageLength() {
        if (messageText != null && messageText.length() > 250) {
            int over = messageText.length() - 250;
            return "Message exceeds 250 characters by " + over + "; please reduce the size.";
        }
        return "Message ready to send.";
    }

    public String createMessageHash() {
        String idPrefix = messageID.substring(0, 2);
        String[] words = messageText.trim().split("\\s+");
        String firstWord = words[0].replaceAll("[^a-zA-Z0-9]", "");
        String lastWord = words[words.length - 1].replaceAll("[^a-zA-Z0-9]", "");
        this.messageHash = idPrefix + ":" + messageNumber + ":"
                + (firstWord + lastWord).toUpperCase();
        return this.messageHash;
    }

    /**
     * @param choice one of "send", "disregard", "store" (case-insensitive)
     */
    public String sendMessage(String choice) {
        if (choice == null) {
            return "Invalid option selected.";
        }
        switch (choice.toLowerCase()) {
            case "send":
                totalMessagesSent++;
                sentMessages.add(this);
                return "Message successfully sent.";
            case "disregard":
                return "Press 0 to delete the message.";
            case "store":
                storeMessage();
                return "Message successfully stored.";
            default:
                return "Invalid option selected.";
        }
    }

    /** Appends this message to messages.json as a simple JSON array entry. */
    public void storeMessage() {
        try {
            String entry = String.format(
                    "{\"messageID\":\"%s\",\"messageHash\":\"%s\",\"recipient\":\"%s\",\"message\":\"%s\"}",
                    escape(messageID), escape(messageHash), escape(recipient), escape(messageText));

            List<String> entries = new ArrayList<>();
            if (Files.exists(JSON_FILE)) {
                String existing = Files.readString(JSON_FILE).trim();
                if (existing.length() > 2) { // more than just "[]"
                    String inner = existing.substring(1, existing.length() - 1).trim();
                    if (!inner.isEmpty()) {
                        for (String e : inner.split("(?<=\\}),(?=\\{)")) {
                            entries.add(e);
                        }
                    }
                }
            }
            entries.add(entry);

            try (FileWriter writer = new FileWriter(JSON_FILE.toFile())) {
                writer.write("[" + String.join(",", entries) + "]");
            }
        } catch (IOException e) {
            System.out.println("Could not save message to JSON: " + e.getMessage());
        }
    }

    private static String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    /** Full details for all messages sent so far, in Message ID / Hash / Recipient / Message order. */
    public static String printMessages() {
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            sb.append(m.fullDetails()).append("\n");
        }
        return sb.toString();
    }

    public String fullDetails() {
        return "Message ID: " + messageID + "\n"
                + "Message Hash: " + messageHash + "\n"
                + "Recipient: " + recipient + "\n"
                + "Message: " + messageText + "\n";
    }

    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    /** Resets shared state between test runs. */
    static void resetForTesting() {
        totalMessagesSent = 0;
        sentMessages.clear();
    }

    public String getMessageID() {
        return messageID;
    }

    public String getMessageHash() {
        return messageHash;
    }
}
