package progettone.listaspesa.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BaseRepository {

	private static final String URL = "jdbc:mysql://localhost:3376/ProgettoAppSpesa";
	private static final String USER = "root";
	private static final String PASSWORD = "root";

	public Connection openConnection() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASSWORD);
	}

}
