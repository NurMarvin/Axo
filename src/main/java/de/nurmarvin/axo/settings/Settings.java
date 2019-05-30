package de.nurmarvin.axo.settings;

public class Settings {
    private ConnectionSettings connectionSettings = new ConnectionSettings();
    private MySQLSettings mySQLSettings = new MySQLSettings();

    public ConnectionSettings connectionSettings() {
        return connectionSettings;
    }

    public MySQLSettings mySQLSettings() {
        return mySQLSettings;
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
