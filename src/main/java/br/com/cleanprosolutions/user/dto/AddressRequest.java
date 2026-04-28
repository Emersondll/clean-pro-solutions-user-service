package br.com.cleanprosolutions.user.dto;

/**
 * DTO representing a physical address.
 *
 * @param street       street name
 * @param number       street number
 * @param complement   apartment or additional info (optional)
 * @param neighborhood neighborhood or district
 * @param city         city name
 * @param state        state abbreviation (e.g., SP)
 * @param zipCode      Brazilian zip code (CEP)
 */
public record AddressRequest(
        String street,
        String number,
        String complement,
        String neighborhood,
        String city,
        String state,
        String zipCode
) {}
