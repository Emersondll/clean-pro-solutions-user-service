package br.com.cleanprosolutions.user.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Embedded sub-document representing a contractor's professional profile.
 *
 * <p>Stored inline within the {@link User} document to avoid extra round-trips.
 * Portfolio photos are stored as URLs pointing to an external object store.</p>
 *
 * @author Clean Pro Solutions Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractorProfile {

    /** Short professional biography visible to clients. */
    private String bio;

    /** Service specialties (e.g. "Deep cleaning", "Office cleaning"). */
    @Builder.Default
    private List<String> specialties = new ArrayList<>();

    /** Public URLs of portfolio photos uploaded by the contractor. */
    @Builder.Default
    private List<String> portfolioPhotos = new ArrayList<>();

    /** Professional certifications or credentials. */
    @Builder.Default
    private List<String> certifications = new ArrayList<>();

    /**
     * Platform verification status.
     *
     * <p>Possible values: {@code UNVERIFIED}, {@code PENDING}, {@code VERIFIED}.</p>
     */
    @Builder.Default
    private String verificationStatus = "UNVERIFIED";
}
