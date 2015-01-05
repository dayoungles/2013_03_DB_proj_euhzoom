package dbclass;

import java.sql.*;

public class Account2 {
	private String url="jdbc:mysql://10.73.44.20/phouse";
	private String user="popi";
	private String pw="db1004";
	

	public Connection connect() {
		Connection con;
		try{
		Class.forName("com.mysql.jdbc.Driver");
		}catch(ClassNotFoundException e){
			System.err.println("driver load error:");
			return null;
		}
		System.out.println("driver loading success");
		try {
			con = DriverManager.getConnection(url,user,pw);
			
			
		}catch (SQLException e){
			System.err.println("connect fail:"+e.getMessage());
			return null;
		}
		System.out.println("connection success");
		return con;
	}

	public void close(Connection con) {
		try {
			con.close();
		} catch (SQLException e) {
			//do nothing;
			e.printStackTrace();
		}
		
	}
	
	public boolean addAccount( String name, int balance) throws SQLException{
		Connection con = connect(); 
		PreparedStatement pstmt;
		String sql = "insert into account_day (name, balance) values (?,?)";//query를 먼저 만들어준다.
		pstmt = con.prepareStatement(sql);
		pstmt.setString(1, name);
		pstmt.setInt(2, balance);
		pstmt.execute();//실행 
		//pstmt.close();// 닫아줘야된다. 
		
		sql = "select * from account_day where name = '"+name+"'"; //string은 ''로 묶어줘야 한다..
		//pstmt = con.prepareStatement(sql);
		
		ResultSet rs  =pstmt.executeQuery(sql);
		
		while(rs.next()){
			if(rs.getString("name").equals("april")){
				pstmt.close();
				con.close();
				return true;
			}
		}
		pstmt.close();
		con.close();
		return false;
	}
	public boolean transferMoney(String from, String to, int money) throws SQLException{
		Connection con = connect();
		con.setAutoCommit(false);
		Statement stmt;
		stmt = con.createStatement();
		String sql = "select balance, name from account_day where name = '"+from+ "'";
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()){
			if(!rs.getString("name").equals(from)){
				stmt.close();
				con.close();
				return false;
			}
			int balance = rs.getInt("balance");
			if(money >balance){
				stmt.close();
				con.rollback();
				con.close();
				return false;
			}
		}
	
		sql = "update account_day set balance = balance- "+money+" where name ='"+from +"'";
		System.out.println(sql);
		stmt.executeUpdate(sql);//  성공한 레코드의 개수가 반영된다.
		
		//if (re !=1)con.rollback;
		sql = "update account_day set balance = balance- "+money+" where name ='"+to +"'";
		//stmt.executeUpdate(sql);
		con.commit();
		stmt.close();
		con.close();
		return true;
	}
}
