package de.nurmarvin.axo.settings;

import java.util.HashMap;
import java.util.Map;

public class Settings {
    private ConnectionSettings connectionSettings = new ConnectionSettings();
    private MySQLSettings mySQLSettings = new MySQLSettings();

    private String exceptionWebHookId = "";
    private String exceptionWebHookToken = "";

    private Map<String, String> apiKeys = new HashMap<>();

    public ConnectionSettings connectionSettings() {
        return connectionSettings;
    }

    public MySQLSettings mySQLSettings() {
        return mySQLSettings;
    }

    public String exceptionWebHookId() {
        return exceptionWebHookId;
    }

    public String exceptionWebHookToken() {
        return exceptionWebHookToken;
    }

    public Map<String, String> apiKeys() {
        return apiKeys;
    }

    public class ConnectionSettings {
        private String token = "";

        public String token() {
            return token;
        }
    }

    public class MySQLSettings {
        private String hostname = "127.0.0.1";
        private int port = 3306;
        private String database = "axo";
        private String username = "";
        private String password = "";

        public String hostname() {
            return hostname;
        }

        public int port() {
            return port;
        }

        public String database() {
            return database;
        }

        public String username() {
            return username;
        }

        public String password() {
            return password;
        }
    }
}
