package br.com.cleanprosolutions.user.enumerations;

/**
 * Enumeration for user types in the Clean Pro Solutions platform.
 *
 * <ul>
 *   <li>{@link #CLIENT} — End user who contracts cleaning/maintenance services</li>
 *   <li>{@link #CONTRACTOR} — Professional who offers services on the platform</li>
 * </ul>
 *
 * @author Clean Pro Solutions Team
 * @since 1.0.0
 */
public enum UserType {

    /** User who requests and contracts services. */
    CLIENT,

    /** Professional who provides cleaning or maintenance services. */
    CONTRACTOR
}
