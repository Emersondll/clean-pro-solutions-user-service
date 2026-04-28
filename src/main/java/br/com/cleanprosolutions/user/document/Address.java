package br.com.cleanprosolutions.user.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embedded document representing a physical address.
 *
 * <p>Used within {@link User} as an embedded sub-document (not a separate collection).</p>
 *
 * @author Clean Pro Solutions Team
 * @since 1.0.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    /** Street name. */
    private String street;

    /** Street number. */
    private String number;

    /** Apartment, suite, or additional location info. */
    private String complement;

    /** Neighborhood or district. */
    private String neighborhood;

    /** City name. */
    private String city;

    /** State abbreviation (e.g., SP, RJ). */
    private String state;

    /** Brazilian zip code (CEP). */
    private String zipCode;
}
