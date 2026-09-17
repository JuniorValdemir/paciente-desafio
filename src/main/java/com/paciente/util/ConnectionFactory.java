package com.paciente.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {

    private static final Properties props = new Properties();

    static {
        try (InputStream input = ConnectionFactory.class.getClassLoader()
                .getResourceAsStream("database.properties")) {

            // Testando cod - System.out.println("Teste de DEBUG");
            if (input == null) {
                throw new RuntimeException("Arquivo 'database.properties' não encontrado em src/main/resources!");
            }

            props.load(input);
            Class.forName(props.getProperty("db.driver"));

        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar configurações do banco de dados", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.user"),
                props.getProperty("db.password")
        );
    }
}