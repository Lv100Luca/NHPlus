package de.hitec.nhplus.model.CreationData;

/**
 * Data class for creating a new user.
 *
 * @param username username of the user
 * @param password password of the user
 */
public record UserCreationData(String username, String password) {
}
