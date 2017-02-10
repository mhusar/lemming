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
     * A role for students.
     */
    public static final String STUDENT = "STUDENT";

    /**
     * A role for users.
     */
    public static final String USER = "USER";

    /**
     * A role of administrators.
     */
    public static final String ADMIN = "ADMIN";

    /**
     * Allowed roles for users.
     */
    public static enum Role {
        STUDENT, USER, ADMIN
    };
}
