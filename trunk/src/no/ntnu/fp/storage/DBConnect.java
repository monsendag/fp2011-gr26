package no.ntnu.fp.storage;

import java.sql.*;

public class DBConnect {
	
	public Connection conn;
	
	public DBConnect() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {

		Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
		conn = DriverManager.getConnection("jdbc:derby:kalenderdb");
		
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("select * from all_tab_columns order by owner, table_name, column_id");
		
		while (rs.next()) {
			System.out.print(rs.getInt(1));
			System.out.print(" ");
			System.out.println(rs.getString(2));
		}
	}
	
	public static void main(String[] args) {
		try {
			DBConnect dbc = new DBConnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
