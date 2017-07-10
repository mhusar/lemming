package lemming.user;

import lemming.auth.SignInPage;
import lemming.auth.UserRoles;
import lemming.auth.WebSession;
import lemming.data.EntityManagerListener;
import lemming.data.GenericDao;
import org.apache.wicket.request.cycle.RequestCycle;
import org.hibernate.StaleObjectStateException;
import org.hibernate.UnresolvableObjectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.UUID;

/**
 * Represents a Data Access Object providing data operations for users.
 */
public class UserDao extends GenericDao<User> implements IUserDao {
    /**
     * Number of iterations to generate a secret key.
     */
    private static final int ITERATIONS = 1024000;

    /**
     * Key length for variable key size ciphers.
     */
    private static final int KEY_LENGTH = 160;

    /**
     * Hashing algorithm used to create a hashed password.
     */
    private static final String ALGORITHM = "PBKDF2WithHmacSHA1";

    /**
     * A logger named corresponding to this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    /**
     * Creates an instance of a UserDao.
     */
    public UserDao() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public Boolean isTransient(User user) {
        return !(user.getId() != null);
    }

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    public void persist(User user) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        if (!(user.getUuid() != null)) {
            user.setUuid(UUID.randomUUID().toString());
        }

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.persist(user);
            transaction.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            if (e instanceof StaleObjectStateException) {
                panicOnSaveLockingError(user, e);
            } else if (e instanceof UnresolvableObjectException) {
                panicOnSaveUnresolvableObjectError(user, e);
            } else {
                throw e;
            }
        } finally {
            entityManager.close();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    public Boolean isDefaultUserNeeded() throws RuntimeException {
        return getAll().isEmpty();
    }

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    public User findByUsername(String username) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<User> query = entityManager.createQuery("FROM User WHERE username = :username ORDER BY realName",
                    User.class);
            List<User> userList = query.setParameter("username", username).getResultList();
            transaction.commit();

            if (userList.isEmpty()) {
                return null;
            } else {
                return userList.get(0);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        } finally {
            entityManager.close();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    public User findByRealName(String realName) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<User> query = entityManager.createQuery("FROM User WHERE realName = :realName ORDER BY realName",
                    User.class);
            List<User> userList = query.setParameter("realName", realName).getResultList();
            transaction.commit();

            if (userList.isEmpty()) {
                return null;
            } else {
                return userList.get(0);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        } finally {
            entityManager.close();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    public List<User> getAll() throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<User> query = entityManager.createQuery("FROM User ORDER BY realName", User.class);
            List<User> userList = query.getResultList();
            transaction.commit();
            return userList;
        } catch (RuntimeException e) {
            e.printStackTrace();

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        } finally {
            entityManager.close();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    public Boolean authenticate(User user, String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (user != null) {
            String hashedPassword = hashPassword(password, user.getSalt());
            Boolean isPasswordEqual = MessageDigest.isEqual(user.getPassword().getBytes(), hashedPassword.getBytes());

            if (user.getEnabled() && isPasswordEqual) {
                logger.info("User #" + user.getId() + " [" + user.getRole().toString() + "] has logged in.");
                return true;
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void logout() {
        User user = WebSession.get().getUser();

        if (user != null) {
            logger.info("User #" + user.getId() + " [" + user.getRole().toString() + "] has logged out.");
        }

        WebSession.get().invalidate();
        RequestCycle.get().setResponsePage(SignInPage.class);
    }

    /**
     * {@inheritDoc}
     */
    public byte[] createRandomSaltBytes() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[20];

        random.nextBytes(saltBytes);
        return saltBytes;
    }

    /**
     * {@inheritDoc}
     *
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    public String hashPassword(String password, byte[] saltBytes)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), saltBytes, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] hash = secretKeyFactory.generateSecret(keySpec).getEncoded();

        return DatatypeConverter.printBase64Binary(hash);
    }

    /**
     * {@inheritDoc}
     *
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @throws RuntimeException
     */
    public void createDefaultUser() throws InvalidKeySpecException, NoSuchAlgorithmException, RuntimeException {
        User defaultUser = new User();
        byte[] saltBytes = createRandomSaltBytes();
        String hashedPassword = hashPassword(DefaultUser.PASSWORD, saltBytes);

        if (hashedPassword != null) {
            defaultUser.setRealName(DefaultUser.REAL_NAME);
            defaultUser.setUsername(DefaultUser.USERNAME);
            defaultUser.setPassword(hashedPassword);
            defaultUser.setSalt(saltBytes);
            defaultUser.setEnabled(true);
            defaultUser.setRole(DefaultUser.ROLE);
            persist(defaultUser);
            logger.info("Default user created");
        }
    }

    /**
     * Definition of a default user.
     */
    private static abstract class DefaultUser {
        /**
         * Real name of default user.
         */
        public static final String REAL_NAME = "Admin User";

        /**
         * Username of default user.
         */
        public static final String USERNAME = "admin";

        /**
         * Password of default user.
         */
        public static final String PASSWORD = "admin";

        /**
         * Role of default user.
         */
        public static final UserRoles.Role ROLE = UserRoles.Role.ADMIN;
    }
}
