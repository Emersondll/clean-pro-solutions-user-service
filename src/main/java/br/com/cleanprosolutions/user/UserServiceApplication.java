package br.com.cleanprosolutions.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Entry point for the Clean Pro Solutions User Service.
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>User profile management (CLIENT and CONTRACTOR)</li>
 *   <li>Geolocation storage and proximity search (MongoDB 2dsphere)</li>
 *   <li>Rating aggregation via RatingCreated events from rating-service</li>
 * </ul>
 *
 * <p>Registers itself with Eureka under the name {@code user-service}.</p>
 *
 * @author Clean Pro Solutions Team
 * @since 1.0.0
 */
@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {

    public static void main(final String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
