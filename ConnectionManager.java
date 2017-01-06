package assignment5;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionManager {
	
	private static Connection Conn = null;
	private static Statement Stmt = null;
	
	public boolean Connect()
	{
		try{
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} 
		catch (ClassNotFoundException exception) {
			System.out.println("Oracle Driver Class Not found: " + exception.toString());
			return false;
		}
		try{
			Conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/xe", "SYSTEM", "aditya");
		} 
		catch (SQLException e) {
			System.out.println("Connection Failed!");
			e.printStackTrace();
			return false;
		}
		try{
			Stmt = (Statement) Conn.createStatement();
		}
		catch(SQLException e) {
			System.out.println("Statement Failed!");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public static Statement getStatement()
	{
		return Stmt;
	}
}
