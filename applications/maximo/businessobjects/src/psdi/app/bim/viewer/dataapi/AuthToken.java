/**
* Copyright IBM Corporation 2009-2024
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
* @Author Doug Wood
* @Updated Java 17 Migration - 2024
**/
package psdi.app.bim.viewer.dataapi;

import java.time.Instant;
import java.time.Duration;

/**
 * Java 17 Record representing an authentication token for Autodesk Forge API.
 * Records provide immutable data carriers with automatic implementations of
 * equals(), hashCode(), and toString().
 * 
 * @param token The authentication token string
 * @param expiresAt The instant when this token expires
 * @param tokenType The type of token (e.g., "Bearer")
 * 
 * @author Doug Wood
 * @version 2.0.0
 * @since Java 17
 */
public record AuthToken(
    String token,
    Instant expiresAt,
    String tokenType
) {
    /**
     * Compact constructor with validation.
     * Ensures token is not null or blank and expiration is in the future.
     */
    public AuthToken {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token cannot be null or blank");
        }
        if (expiresAt == null) {
            throw new IllegalArgumentException("Expiration time cannot be null");
        }
        if (tokenType == null || tokenType.isBlank()) {
            tokenType = "Bearer"; // Default token type
        }
    }
    
    /**
     * Convenience constructor with default token type.
     */
    public AuthToken(String token, Instant expiresAt) {
        this(token, expiresAt, "Bearer");
    }
    
    /**
     * Checks if the token has expired.
     * 
     * @return true if the token has expired, false otherwise
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
    
    /**
     * Checks if the token is still valid (not expired).
     * 
     * @return true if the token is valid, false otherwise
     */
    public boolean isValid() {
        return !isExpired();
    }
    
    /**
     * Gets the remaining time until expiration.
     * 
     * @return Duration until expiration, or Duration.ZERO if already expired
     */
    public Duration getTimeUntilExpiration() {
        Instant now = Instant.now();
        if (now.isAfter(expiresAt)) {
            return Duration.ZERO;
        }
        return Duration.between(now, expiresAt);
    }
    
    /**
     * Gets the number of seconds until expiration.
     * 
     * @return seconds until expiration, or 0 if already expired
     */
    public long getSecondsUntilExpiration() {
        return getTimeUntilExpiration().getSeconds();
    }
    
    /**
     * Creates a new AuthToken with extended expiration time.
     * 
     * @param additionalTime Duration to add to current expiration
     * @return new AuthToken with extended expiration
     */
    public AuthToken extendExpiration(Duration additionalTime) {
        return new AuthToken(token, expiresAt.plus(additionalTime), tokenType);
    }
    
    /**
     * Gets the authorization header value.
     * 
     * @return formatted authorization header (e.g., "Bearer token_value")
     */
    public String getAuthorizationHeader() {
        return tokenType + " " + token;
    }
}

// Made with Bob
