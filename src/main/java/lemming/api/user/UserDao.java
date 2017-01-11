package lemming.api.user;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.xml.bind.DatatypeConverter;

import lemming.api.data.EntityManagerListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.hibernate.StaleObjectStateException;
import org.hibernate.UnresolvableObjectException;

import lemming.api.auth.SignInPage;
import lemming.api.auth.UserRoles;
import lemming.api.auth.WebSession;
import lemming.api.data.GenericDao;

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
    private static final Logger logger = Logger.getLogger(UserDao.class.getName());

    /**
     * Creates an instance of a UserDao.
     */
    public UserDao() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isTransient(User user) {
        return !(user.getId() instanceof Integer);
    }

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    public void persist(User user) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        if (!(user.getUuid() instanceof String)) {
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
    @Override
    public Boolean isDefaultUserNeeded() throws RuntimeException {
        return getAll().isEmpty();
    }

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException
     */
    @Override
    public User findByUsername(String username) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<User> query = entityManager.createQuery("FROM User WHERE username = :username", User.class);
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
    @Override
    public User findByRealName(String realName) throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<User> query = entityManager.createQuery("FROM User WHERE realName = :realName", User.class);
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
    @Override
    public List<User> getAll() throws RuntimeException {
        EntityManager entityManager = EntityManagerListener.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TypedQuery<User> query = entityManager.createQuery("FROM User ORDER BY realName ASC", User.class);
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
    @Override
    public Boolean authenticate(User user, String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (user instanceof User) {
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

        if (user instanceof User) {
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
    @Override
    public void createDefaultUser() throws InvalidKeySpecException, NoSuchAlgorithmException, RuntimeException {
        User defaultUser = new User();
        byte[] saltBytes = createRandomSaltBytes();
        String hashedPassword = hashPassword(DefaultUser.PASSWORD, saltBytes);

        if (hashedPassword instanceof String) {
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
