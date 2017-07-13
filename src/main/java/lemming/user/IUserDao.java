package lemming.user;

import lemming.data.IDao;

/**
 * Defines a user DAO by extending interface IDao.
 */
public interface IUserDao extends IDao<User> {
    /**
     * Checks if a default user is needed.
     *
     * @return True if no user is found.
     */
    Boolean isDefaultUserNeeded();

    /**
     * Returns the matching user for a given username.
     *
     * @param username the username of a user
     * @return The matching user or null.
     */
    User findByUsername(String username);

    /**
     * Returns the matching user for a given real name.
     *
     * @param realName the real name of a user
     * @return The matching user or null.
     */
    User findByRealName(String realName);

    /**
     * Authenticates a user by password.
     *
     * @param user     a valid user
     * @param password a matching password
     * @return True if authentication worked; false otherwise.
     */
    Boolean authenticate(User user, String password) throws Exception;

    /**
     * Logs out the user associated with the current session.
     */
    void logout();

    /**
     * Creates a random array of bytes.
     *
     * @return An array of bytes.
     */
    byte[] createRandomSaltBytes();

    /**
     * Hashes a password with a given salt value.
     *
     * @param password  the given password
     * @param saltBytes the given salt bytes.
     * @return A hashed password or null if hashing failed.
     */
    String hashPassword(String password, byte[] saltBytes) throws Exception;

    /**
     * Creates a default user.
     */
    void createDefaultUser() throws Exception;
}
