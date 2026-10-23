package com.chatapp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageTest {

    @BeforeEach
    public void setUp() {
        Message.resetForTesting();
    }

    @AfterEach
    public void tearDown() {
        Message.resetForTesting();
    }

    // ---------- Message length (not more than 250 characters) ----------

    @Test
    public void testMessageLength_Success() {
        Message message = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?", 0);
        assertEquals("Message ready to send.", message.checkMessageLength());
    }

    @Test
    public void testMessageLength_Failure() {
        String longText = "A".repeat(260); // 10 characters over the 250 limit
        Message message = new Message("+27718693002", longText, 0);
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.", message.checkMessageLength());
    }

    // ---------- Recipient cell number (reused from Part 1's cell check) ----------

    @Test
    public void testRecipientCell_CorrectlyFormatted() {
        Message message = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?", 0);
        assertEquals("Cell phone number successfully captured.", message.checkRecipientCell());
    }

    @Test
    public void testRecipientCell_IncorrectlyFormatted() {
        Message message = new Message("08575975889", "Hi Keegan, did you receive the payment?", 1);
        assertEquals("Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.",
                message.checkRecipientCell());
    }

    // ---------- Message hash ----------

    @Test
    public void testMessageHash_TestCase1() {
        // Fixed message ID so the "00" prefix in the brief's example is reproducible
        Message message = new Message("0012345678", "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 0);
        assertEquals("00:0:HITONIGHT", message.createMessageHash());
    }

    @Test
    public void testMessageHash_RemainderInLoop() {
        // Extra hash scenarios, checked in a loop as the brief asks for
        String[][] cases = {
                // messageID, messageNumber, text, expectedHash
                {"0012345678", "1", "Hi Keegan, did you receive the payment?", "00:1:HIPAYMENT"},
                {"5598765432", "2", "Thanks for the update today", "55:2:THANKSTODAY"},
                {"7711223344", "3", "Can we meet tomorrow", "77:3:CANTOMORROW"},
        };

        for (String[] testCase : cases) {
            Message message = new Message(testCase[0], "+27718693002", testCase[2],
                    Integer.parseInt(testCase[1]));
            assertEquals(testCase[3], message.createMessageHash());
        }
    }

    // ---------- Message ID ----------

    @Test
    public void testMessageIdConfirmation() {
        Message message = new Message("1234567890", "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 0);
        assertEquals("Message ID generated: 1234567890", message.messageIdConfirmation());
    }

    // ---------- sendMessage() choices ----------

    @Test
    public void testSendMessage_Send() {
        Message message = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?", 0);
        assertEquals("Message successfully sent.", message.sendMessage("send"));
    }

    @Test
    public void testSendMessage_Disregard() {
        Message message = new Message("08575975889", "Hi Keegan, did you receive the payment?", 1);
        assertEquals("Press 0 to delete the message.", message.sendMessage("disregard"));
    }

    @Test
    public void testSendMessage_Store() {
        Message message = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?", 0);
        message.createMessageHash();
        assertEquals("Message successfully stored.", message.sendMessage("store"));
    }

    // ---------- Totals, using the brief's two-message scenario ----------

    @Test
    public void testReturnTotalMessages_AfterSendAndDiscard() {
        Message message1 = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?", 0);
        message1.sendMessage("send"); // Test Data for Task 1: Select Send
        assertEquals(1, Message.returnTotalMessages());

        Message message2 = new Message("08575975889", "Hi Keegan, did you receive the payment?", 1);
        message2.sendMessage("disregard"); // Test Data for Message 2: Select Discard
        assertEquals(1, Message.returnTotalMessages()); // unchanged, since it wasn't sent
    }
}
