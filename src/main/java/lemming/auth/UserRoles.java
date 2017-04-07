package lemming.auth;

/**
 * Class defining allowed roles for users.
 */
public abstract class UserRoles {
    /**
     * An artificial role for signed in users.
     */
    public static final String SIGNED_IN = "SIGNED_IN";

    /**
     * A role of administrators.
     */
    public static final String ADMIN = "ADMIN";

    /**
     * Allowed roles for users.
     */
    public enum Role {
        STUDENT, USER, ADMIN
    }
}
