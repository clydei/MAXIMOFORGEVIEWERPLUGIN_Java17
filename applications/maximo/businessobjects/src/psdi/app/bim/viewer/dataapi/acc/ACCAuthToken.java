/**
 * Copyright IBM Corporation 2009-2026
 *
 * Licensed under the Eclipse Public License - v 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @Author ACC Plugin Development Team
 * @Since Java 17
 **/
package psdi.app.bim.viewer.dataapi.acc;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;

/**
 * Java 17 Record for Autodesk Construction Cloud OAuth 2.0 authentication token.
 * This immutable, thread-safe record represents an ACC access token with built-in validation.
 * 
 * Features:
 * - Immutable by design (Java 17 Record)
 * - Thread-safe
 * - Built-in validation in compact constructor
 * - Expiration checking
 * - Authorization header generation
 * 
 * @param accessToken The OAuth 2.0 access token
 * @param refreshToken The OAuth 2.0 refresh token (optional)
 * @param expiresAt The instant when the token expires
 * @param tokenType The token type (typically "Bearer")
 * @param scopes The set of granted OAuth scopes
 */
public record ACCAuthToken(
    String accessToken,
    String refreshToken,
    Instant expiresAt,
    String tokenType,
    Set<String> scopes
) {
    /**
     * Compact constructor with validation.
     * Ensures all required fields are present and valid.
     */
    public ACCAuthToken {
        Objects.requireNonNull(accessToken, "Access token cannot be null");
        Objects.requireNonNull(expiresAt, "Expiration time cannot be null");
        
        if (accessToken.isEmpty()) {
            throw new IllegalArgumentException("Access token cannot be empty");
        }
        
        // Set defaults for optional fields
        tokenType = (tokenType != null && !tokenType.isEmpty()) ? tokenType : "Bearer";
        scopes = (scopes != null) ? Set.copyOf(scopes) : Set.of();
    }
    
    /**
     * Convenience constructor without refresh token.
     */
    public ACCAuthToken(String accessToken, Instant expiresAt, String tokenType, Set<String> scopes) {
        this(accessToken, null, expiresAt, tokenType, scopes);
    }
    
    /**
     * Convenience constructor with default token type.
     */
    public ACCAuthToken(String accessToken, Instant expiresAt, Set<String> scopes) {
        this(accessToken, null, expiresAt, "Bearer", scopes);
    }
    
    /**
     * Check if the token has expired.
     * 
     * @return true if the current time is after the expiration time
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
    
    /**
     * Check if the token is valid (not expired and has a non-empty access token).
     * 
     * @return true if the token is valid
     */
    public boolean isValid() {
        return !isExpired() && accessToken != null && !accessToken.isEmpty();
    }
    
    /**
     * Check if the token will expire within the specified duration.
     * 
     * @param duration The duration to check
     * @return true if the token will expire within the duration
     */
    public boolean expiresWithin(Duration duration) {
        Instant threshold = Instant.now().plus(duration);
        return expiresAt.isBefore(threshold);
    }
    
    /**
     * Get the authorization header value for HTTP requests.
     * 
     * @return The formatted authorization header (e.g., "Bearer abc123...")
     */
    public String getAuthorizationHeader() {
        return tokenType + " " + accessToken;
    }
    
    /**
     * Get the number of seconds until the token expires.
     * 
     * @return Seconds until expiration (negative if already expired)
     */
    public long getSecondsUntilExpiration() {
        return Duration.between(Instant.now(), expiresAt).getSeconds();
    }
    
    /**
     * Check if the token has a specific scope.
     * 
     * @param scope The scope to check
     * @return true if the token has the scope
     */
    public boolean hasScope(String scope) {
        return scopes.contains(scope);
    }
    
    /**
     * Check if the token has all specified scopes.
     * 
     * @param requiredScopes The scopes to check
     * @return true if the token has all scopes
     */
    public boolean hasAllScopes(Set<String> requiredScopes) {
        return scopes.containsAll(requiredScopes);
    }
    
    /**
     * Create a new token with extended expiration.
     * 
     * @param extension The duration to extend
     * @return A new ACCAuthToken with extended expiration
     */
    public ACCAuthToken extendExpiration(Duration extension) {
        return new ACCAuthToken(
            accessToken,
            refreshToken,
            expiresAt.plus(extension),
            tokenType,
            scopes
        );
    }
    
    /**
     * Create a new token with a different access token (after refresh).
     * 
     * @param newAccessToken The new access token
     * @param newExpiresAt The new expiration time
     * @return A new ACCAuthToken with updated values
     */
    public ACCAuthToken withNewAccessToken(String newAccessToken, Instant newExpiresAt) {
        return new ACCAuthToken(
            newAccessToken,
            refreshToken,
            newExpiresAt,
            tokenType,
            scopes
        );
    }
    
    /**
     * Get a masked version of the token for logging (shows only first/last 4 chars).
     * 
     * @return Masked token string
     */
    public String getMaskedToken() {
        if (accessToken == null || accessToken.length() < 8) {
            return "****";
        }
        return accessToken.substring(0, 4) + "..." + 
               accessToken.substring(accessToken.length() - 4);
    }
    
    @Override
    public String toString() {
        return "ACCAuthToken[" +
               "token=" + getMaskedToken() +
               ", expiresAt=" + expiresAt +
               ", tokenType=" + tokenType +
               ", scopes=" + scopes +
               ", valid=" + isValid() +
               "]";
    }
}

// Made with Bob
