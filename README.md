# Chat App PoE — Part 1: Registration and Login

## What's in this project
- `src/main/java/com/chatapp/Login.java` — the required `Login` class: `checkUserName()`,
  `checkPasswordComplexity()`, `checkCellPhoneNumber()`, `registerUser()`, `loginUser()`,
  `returnLoginStatus()`.
- `src/main/java/com/chatapp/Main.java` — a small console app so the program actually
  takes input and prints output (registers a user, then logs them in).
- `src/test/java/com/chatapp/LoginTest.java` — JUnit 5 tests using the exact test data
  given in the brief.
- `pom.xml` — Maven project file (Java 17, JUnit 5).

## Two judgement calls I made (be ready to explain these in your video)
1. **First name / last name.** The registration step in the brief only lists username,
   password and cell number, but the login success message needs a first name and last
   name ("Welcome `<first name>`, `<last name>` ..."). I added those two as extra
   registration fields since there's no other way to produce that message. If your
   lecturer meant something different, this is the easiest thing to change.
2. **`registerUser()` success message.** The brief describes what the failure messages
   should say but doesn't give exact wording for the success case, so I combined the
   three "successfully captured" messages into one string. Check this against your
   rubric — if your lecturer wants a shorter success message, it's a one-line change.

There's also a small inconsistency in the brief itself worth knowing about: the "Username
is correctly formatted" row in the unit test table shows the *login welcome message* as
the expected result, which doesn't match how `checkUserName()`/`registerUser()` are
described everywhere else in the document. I used "Username successfully captured." for
that case instead, matching the earlier table and the pattern of the other rows — flag
this to your lecturer if your actual mark allocation differs.

## Regex references
- Password complexity pattern: lookahead-based approach as commonly described in
  Baeldung's "Java Regex Password Validation" guide.
- Cell number format: general international-number guidance from the article linked in
  the brief (quickblox.com/blog/beginners-guide-to-chat-app-architecture/).

## Opening this in NetBeans
1. NetBeans → File → Open Project → select this folder (NetBeans recognises `pom.xml`
   automatically as a Maven project).
2. Right-click the project → Clean and Build to fetch JUnit and compile.
3. Run `Main.java` to try the console app; run `LoginTest.java` (right-click → Test File)
   to run the unit tests.

## Before you submit
- Push to GitHub with at least six commits for this part.
- Record your (unlisted, own-voice, no AI voice) video walking through the code and
  explaining the logic — the two judgement calls above are good talking points.
