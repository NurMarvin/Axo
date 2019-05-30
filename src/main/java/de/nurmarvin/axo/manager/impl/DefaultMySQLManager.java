package de.nurmarvin.axo.manager.impl;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import de.nurmarvin.axo.AxoDiscordBot;
import de.nurmarvin.axo.manager.MySQLManager;
import de.nurmarvin.axo.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public final class DefaultMySQLManager implements MySQLManager {
    private ConnectionSource connectionSource;
    private static final Logger LOGGER = LoggerFactory.getLogger(MySQLManager.class);

    public DefaultMySQLManager() {
        long start = System.currentTimeMillis();
        Settings.MySQLSettings mySQLSettings = AxoDiscordBot.instance().settings().mySQLSettings();
        String databaseUrl = String.format("jdbc:mysql://%s:%d/%s", mySQLSettings.hostname(),
                                           mySQLSettings.port(), mySQLSettings.database());
        try {
            connectionSource = new JdbcConnectionSource(databaseUrl, mySQLSettings.username(),
                                                        mySQLSettings.password());
            LOGGER.info("MySQL connection established successfully in {}ms.", System.currentTimeMillis() - start);
        } catch (SQLException e) {
            LOGGER.error("Error while establishing a MySQL connection", e);
        }
    }

    @Override
    public ConnectionSource connectionSource() {
        return connectionSource;
    }

    public <D extends Dao<T, ?>, T> D createDao(Class<?> clazz) {
        Dao<?, ?> dao = null;
        try {
            dao = DaoManager.createDao(connectionSource, clazz);
            LOGGER.info("Successfully created dao for class {}", clazz.getSimpleName());
        } catch (SQLException e) {
            LOGGER.error(String.format("Error while creating dao for class %s", clazz.getSimpleName()), e);
        }
        @SuppressWarnings("unchecked")
        D castedDao = (D) dao;

        try {
            TableUtils.createTableIfNotExists(connectionSource, clazz);
        } catch (SQLException e) {
            LOGGER.error(String.format("Error while creating table for class %s", clazz.getSimpleName()), e);
        }

        return castedDao;
    }
}
