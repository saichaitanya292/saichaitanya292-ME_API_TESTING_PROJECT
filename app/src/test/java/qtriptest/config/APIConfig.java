package qtriptest.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Centralized configuration class for API testing
 * Manages all API endpoints, credentials, and settings
 */
public class APIConfig {
    
    private static APIConfig instance;
    private Properties properties;
    
    // API Configuration Constants
    private static final String BASE_URI = "https://content-qtripdynamic-qa-backend.azurewebsites.net/api/v1";
    private static final String REGISTER_ENDPOINT = "/register";
    private static final String LOGIN_ENDPOINT = "/login";
    private static final int EXPECTED_STATUS_CODE_SUCCESS = 201;
    private static final int EXPECTED_STATUS_CODE_UNAUTHORIZED = 401;
    private static final int EXPECTED_STATUS_CODE_OK = 200;
    private static final int EXPECTED_STATUS_CODE_BAD_REQUEST = 400;
    
    // Test Data Configuration
    private static final String TEST_PASSWORD = "s@gmail.com";
    private static final String TEST_EMAIL_PREFIX = "testuser";
    private static final String TEST_EMAIL_DOMAIN = "@gmail.com";
    
    // API Response Configuration
    private static final int CONNECTION_TIMEOUT = 10000; // 10 seconds
    private static final int READ_TIMEOUT = 10000; // 10 seconds
    
    // Request Headers
    private static final String CONTENT_TYPE_JSON = "application/json";
    
    // Constructor (Private for Singleton)
    private APIConfig() {
        this.properties = new Properties();
        loadProperties();
    }
    
    /**
     * Get singleton instance of APIConfig
     */
    public static synchronized APIConfig getInstance() {
        if (instance == null) {
            instance = new APIConfig();
        }
        return instance;
    }
    
    /**
     * Load properties from external configuration file (if exists)
     */
    private void loadProperties() {
        try {
            String configFile = System.getProperty("config.file", "config.properties");
            FileInputStream fileInputStream = new FileInputStream(configFile);
            properties.load(fileInputStream);
            fileInputStream.close();
        } catch (IOException e) {
            System.out.println("Config file not found. Using default values.");
        }
    }
    
    // ==================== API Endpoints ====================
    public String getBaseURI() {
        return properties.getProperty("api.baseURI", BASE_URI);
    }
    
    public String getRegisterEndpoint() {
        return properties.getProperty("api.register.endpoint", REGISTER_ENDPOINT);
    }
    
    public String getLoginEndpoint() {
        return properties.getProperty("api.login.endpoint", LOGIN_ENDPOINT);
    }
    
    // ==================== Status Codes ====================
    public int getSuccessStatusCode() {
        return Integer.parseInt(properties.getProperty("api.statusCode.success", String.valueOf(EXPECTED_STATUS_CODE_SUCCESS)));
    }
    
    public int getUnauthorizedStatusCode() {
        return Integer.parseInt(properties.getProperty("api.statusCode.unauthorized", String.valueOf(EXPECTED_STATUS_CODE_UNAUTHORIZED)));
    }
    
    public int getOkStatusCode() {
        return Integer.parseInt(properties.getProperty("api.statusCode.ok", String.valueOf(EXPECTED_STATUS_CODE_OK)));
    }
    
    public int getBadRequestStatusCode() {
        return Integer.parseInt(properties.getProperty("api.statusCode.badRequest", String.valueOf(EXPECTED_STATUS_CODE_BAD_REQUEST)));
    }
    
    // ==================== Test Data ====================
    public String getTestPassword() {
        return properties.getProperty("test.password", TEST_PASSWORD);
    }
    
    public String getTestEmailPrefix() {
        return properties.getProperty("test.email.prefix", TEST_EMAIL_PREFIX);
    }
    
    public String getTestEmailDomain() {
        return properties.getProperty("test.email.domain", TEST_EMAIL_DOMAIN);
    }
    
    public String generateTestEmail(String uniqueId) {
        return getTestEmailPrefix() + uniqueId + getTestEmailDomain();
    }
    
    // ==================== Timeouts ====================
    public int getConnectionTimeout() {
        return Integer.parseInt(properties.getProperty("api.timeout.connection", String.valueOf(CONNECTION_TIMEOUT)));
    }
    
    public int getReadTimeout() {
        return Integer.parseInt(properties.getProperty("api.timeout.read", String.valueOf(READ_TIMEOUT)));
    }
    
    // ==================== Headers ====================
    public String getContentTypeJson() {
        return properties.getProperty("api.header.contentType.json", CONTENT_TYPE_JSON);
    }
    
    // ==================== Environment Configuration ====================
    public String getEnvironment() {
        return properties.getProperty("test.environment", "qa");
    }
    
    public boolean isDebugMode() {
        return Boolean.parseBoolean(properties.getProperty("test.debug.enabled", "false"));
    }
    
    // ==================== Utility Methods ====================
    /**
     * Reset singleton instance (useful for testing)
     */
    public static void resetInstance() {
        instance = null;
    }
    
    /**
     * Get all configuration values (for debugging)
     */
    @Override
    public String toString() {
        return "APIConfig{" +
                "baseURI='" + getBaseURI() + '\'' +
                ", registerEndpoint='" + getRegisterEndpoint() + '\'' +
                ", loginEndpoint='" + getLoginEndpoint() + '\'' +
                ", successStatusCode=" + getSuccessStatusCode() +
                ", unauthorizedStatusCode=" + getUnauthorizedStatusCode() +
                ", testPassword='" + getTestPassword() + '\'' +
                ", environment='" + getEnvironment() + '\'' +
                ", debugMode=" + isDebugMode() +
                '}';
    }
}
