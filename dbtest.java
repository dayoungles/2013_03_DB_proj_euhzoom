package dbclass;

import java.io.IOException;
import java.sql.*;

public class dbtest {
	public static void main(String[] args) throws IOException {
		String addr = "jdbc:mysql://10.73.44.20/phouse";
		String user = "popi";
		String pw = "db1004";

		Connection conn;
		PreparedStatement pstmt;
		Statement stmt;
		ResultSet rs;
		String sql;

		try {
			Class.forName("com.mysql.jdbc.Driver");

		} catch (ClassNotFoundException e) {
			System.err.println("Driver Error: " + e.getMessage());
			return;
		}
		System.out.println("Driver Loading Success");

		try {
			conn = DriverManager.getConnection(addr, user, pw); //서버랑 db랑 연결 
			// unset auto commit;
			conn.setAutoCommit(false);
			String psql = "update account set money =  money + ? where id = ?";
			pstmt = conn.prepareStatement(psql);
			pstmt.setString(2, "sa");
			pstmt.setInt(1, 5000);
			pstmt.execute();

			pstmt.setString(2, "gle");
			pstmt.setInt(1, 10000);
			pstmt.execute();

			stmt = conn.createStatement();
			sql = "select * from account";
			rs = stmt.executeQuery(sql);
			System.out.println("before rollback or commit");

			while (rs.next()) {
				String id = rs.getString("id");
				int money = rs.getInt("money");
				System.out.printf("%s : %d\n", id, money);
			}

			// pause
			System.out.print("press enter key..");
			int x = System.in.read();

			System.out.println("after rollback");
			// conn.commit();
			conn.rollback();
			sql = "select * from account";
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				String id = rs.getString("id");
				int money = rs.getInt("money");
				System.out.printf("%s : %d\n", id, money);
			}

			pstmt.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.err.println("Mysql Connection error: " + e.getMessage());
			return;
		}
		// System.out.println("Connection Success");
	}

}