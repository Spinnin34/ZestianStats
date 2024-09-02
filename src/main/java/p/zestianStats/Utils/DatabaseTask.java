package p.zestianStats.Utils;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class DatabaseTask implements Runnable {

    @Override
    public final void run() {
        try (Connection connection = getConnection()) {
            if (connection == null || connection.isClosed()) {
                throw new SQLException("No se pudo obtener la conexión a la base de datos.");
            }
            execute(connection);
        } catch (SQLException e) {
            e.printStackTrace(); // Log excepción SQL
        }
    }

    protected abstract Connection getConnection() throws SQLException;

    protected abstract void execute(Connection connection) throws SQLException;
}


