package br.com.cleanprosolutions.user.dto;

import java.util.List;

/**
 * Request payload for updating a contractor's professional profile.
 *
 * @param bio              short professional biography
 * @param specialties      list of service specialties
 * @param portfolioPhotos  public URLs of portfolio photos
 * @param certifications   professional certifications or credentials
 *
 * @author Emerson Lima
 * @since 1.0.0
 */
public record ContractorProfileRequest(
        String bio,
        List<String> specialties,
        List<String> portfolioPhotos,
        List<String> certifications
) {}
