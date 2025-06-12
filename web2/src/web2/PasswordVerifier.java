package web2;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordVerifier {

    public static void main(String[] args) {
        String storedHashedPassword = "afupCnF4gPTjaJAH2IsPSfamKN4SZz8jRdKGEZxa0Kw="; // This would be retrieved from your database

        // Simulate a user entering a password
        String enteredPasswordAttempt1 = "mySecretPassword123"; // The actual password
        String enteredPasswordAttempt2 = "mySecretPassword123";

        // To verify:
        boolean passwordMatches1 = BCrypt.checkpw(enteredPasswordAttempt1, storedHashedPassword);
        boolean passwordMatches2 = BCrypt.checkpw(enteredPasswordAttempt2, storedHashedPassword);
 
        System.out.println("Does '" + enteredPasswordAttempt1 + "' match the stored hash? " + passwordMatches1);
        System.out.println("Does '" + enteredPasswordAttempt2 + "' match the stored hash? " + passwordMatches2);

        // If you were generating a new hash for a new user:
        String newPassword = "anotherNewPassword";
        String generatedHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        System.out.println("Generated hash for '" + newPassword + "': " + generatedHash);
    }
}