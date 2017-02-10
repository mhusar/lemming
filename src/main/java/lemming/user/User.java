package lemming.user;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.annotations.SelectBeforeUpdate;

import lemming.auth.UserRoles;

/**
 * Represents a user with one role.
 */
@BatchSize(size = 30)
@DynamicUpdate
@Entity
@OptimisticLocking(type = OptimisticLockType.VERSION)
@SelectBeforeUpdate
@Table(name = "user", indexes = { @Index(columnList = "uuid", unique = true),
        @Index(columnList = "username", unique = true),
        @Index(columnList = "real_name", unique = true) })
public class User implements Serializable {
    /**
     * Determines if a deserialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * ID associated with a user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * A UUID used to distinguish users.
     */
    @Column(name = "uuid")
    private String uuid;

    /**
     * Version number field used for optimistic locking.
     */
    @Column(name = "version")
    @Version
    private Long version;

    /**
     * Real name of a user.
     */
    @Column(name = "real_name", nullable = false)
    private String realName;

    /**
     * The username associated with a user.
     */
    @Column(name = "username", nullable = false)
    private String username;

    /**
     * Hashed password of a user.
     * 
     * @see UserDao#hashPassword(String, byte[])
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * Salt bytes used to hash a user’s password.
     * 
     * @see UserDao#createRandomSaltBytes()
     */
    @Lob
    @Column(name = "salt", nullable = false)
    private byte[] salt;

    /**
     * Role of a user.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRoles.Role role;

    /**
     * Defines if a user is able to log in.
     */
    @Column(name = "is_enabled")
    private Boolean enabled;

    /**
     * Creates an instance of a user.
     */
    public User() {
    }

    /**
     * Returns the ID associated with a user.
     * 
     * @return Primary key of a user.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the ID of a user.
     * 
     * @param id
     *            the ID of a user
     */
    @SuppressWarnings("unused")
    private void setId(Integer id) {
        this.id = id;
    }

    /**
     * Returns the UUID of a user.
     * 
     * @return UUID of a user.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Sets the UUID of a user.
     * 
     * @param uuid
     *            the UUID of a user
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Returns the version of a user.
     * 
     * @return Version number of a user.
     */
    @SuppressWarnings("unused")
    private Long getVersion() {
        return version;
    }

    /**
     * Sets the version number of a user.
     * 
     * @param version
     *            version number of a user
     */
    @SuppressWarnings("unused")
    private void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Returns the real name of a user.
     * 
     * @return A user’s real name.
     */
    public String getRealName() {
        return realName;
    }

    /**
     * Sets the real name of a user.
     * 
     * @param realName
     *            a real name
     */
    public void setRealName(String realName) {
        this.realName = realName;
    }

    /**
     * Returns the username of a user.
     * 
     * @return The user’s username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of a user.
     * 
     * @param username
     *            a username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the hashed password of a user.
     * 
     * @return A user’s hashed password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the hashed password of the user.
     * 
     * @param password
     *            a hashed password
     * @see UserDao#hashPassword(String, byte[])
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Salt bytes used to hash a user’s password.
     * 
     * @return A salt byte array.
     */
    public byte[] getSalt() {
        return salt;
    }

    /**
     * Sets the salt used to hash a user’s password.
     * 
     * @param salt
     *            a byte array
     * @see UserDao#createRandomSaltBytes()
     */
    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    /**
     * Returns role of a user.
     * 
     * @return The role of a user.
     */
    public UserRoles.Role getRole() {
        return role;
    }

    /**
     * Sets a user’s role.
     * 
     * @param role
     *            the role of a user
     */
    public void setRole(UserRoles.Role role) {
        this.role = role;
    }

    /**
     * Returns the enabled status of a user.
     * 
     * @return True if the user is enabled; false otherwise.
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * Sets the enabled status of the user.
     * 
     * @param enabled
     *            true or false
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Indicates if some other object is equal to this one.
     * 
     * @param object
     *            the reference object with which to compare
     * @return True if this object is the same as the object argument; false
     *         otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || !(object instanceof User))
            return false;

        User user = (User) object;

        if (!(uuid instanceof String)) {
            uuid = UUID.randomUUID().toString();
        }

        return uuid.equals(user.getUuid());
    }

    /**
     * Returns a hash code value for a user.
     * 
     * @return A hash code value for a user.
     */
    @Override
    public int hashCode() {
        if (!(uuid instanceof String)) {
            uuid = UUID.randomUUID().toString();
        }

        return uuid.hashCode();
    }
}
