package dbclass;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class account {
	static Connection conn;
	
	public static void main(String[] args) {
		String addr = "jdbc:mysql://10.73.44.20/phouse";
		String user = "popi";
		String pw = "db1004";

		//Connection conn;
		

		try {
			Class.forName("com.mysql.jdbc.Driver");

		} catch (Exception e) {
			System.err.println("Driver Error" + e.getMessage());
			return;
		}
		System.out.println("Driver Loading Success");

		try { 
			conn = DriverManager.getConnection(addr, user, pw);
			conn.setAutoCommit(false);
			
			run();

		} catch (Exception e) {
			System.out.println("error?");
		}
	}

	public static String menuPrint() {
		System.out.println("Select menu");
		System.out.println("1. Create account");
		System.out.println("2. Check my balance");
		System.out.println("3. Transfer money");
		System.out.println("Input menu number");
		
		Scanner menu = new Scanner(System.in);
		String menu1 = menu.next();
		return menu1;
	}

	public static void run() {
		String doit = menuPrint();
		
		if (doit.equals("1")) {
			createAccount();
		} else if (doit.equals("2")) {
			checkBalance();
		} else if (doit.equals("3")) {
			transfer();
		} else {
			System.out.println("Input proper Menu Number");
		}
	}

	private static void createAccount(){
		try {
			PreparedStatement pstmt;
			String create = "insert into account_day (name, balance) values(? , ?)";
			pstmt = conn.prepareStatement(create);

			System.out.println("input name");
			Scanner name = new Scanner(System.in);
			
			String n = name.next();
			pstmt.setString(1, n);

			System.out.println("input money");
			Scanner money = new Scanner(System.in);
			int m = money.nextInt();
			pstmt.setInt(2, m);
			pstmt.execute();
			
			stmt = conn.createStatement();
			sql = "select * from account_day ";
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				String id = rs.getString("name");
				int balance = rs.getInt("balance");
				System.out.printf("%s : %d\n", id, balance);
			}
			System.out.println("commit?");
			Scanner confirm = new Scanner (System.in);
			if(confirm.next().equals("yes")){
				conn.commit();
			} else {
				conn.rollback();
			}
		
		pstmt.close();
		} catch (Exception e) {
			System.out.println("createAccount error");
		}
	}

	private static void checkBalance() {
		try {
			PreparedStatement pstmt;
			
			
			String query = "select * from account_day where name = ?";
			pstmt = conn.prepareStatement(query);
			
			
			System.out.println("input name");
			Scanner name = new Scanner(System.in);
			String n = name.next();
			pstmt.setString(1, n);
			pstmt.execute();
			
			Statement stmt = conn.createStatement();
			stmt.execute("select * from aaa");
			stmt.execute("smeme")
			stmt.close();
			
			String sql = "select name, balance from account_day where name = ?";
			
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, n);
			rs = pstmt.executeQuery();
			
			
			//rs = stmt.executeQuery(sql);

			while (rs.next()) {
				String id = rs.getString("name");
				int balance = rs.getInt("balance");
				System.out.printf("%s 's balance is : %d\n", id, balance);
			}
		} catch (Exception e) {
			System.out.println("checkBalance error");
			e.printStackTrace();
		}
	}

	private static void transfer() {
		try {
			PreparedStatement pstmt;
			Statement stmt;
			ResultSet rs;
			ResultSet receiverRs;
			String query = "select * from account_day where name = ?";
			//이름을 삽입한 잔액 조회 쿼리 작성.
			System.out.println("Input your name, money to transfer, receiver's name");
			Scanner name = new Scanner(System.in);
			String n = name.next();
			int money = name.nextInt();
			String receiver = name.next();
			
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, n);
			rs = pstmt.executeQuery();
			rs.next() ;
			if(rs.getInt("balance")< money){
				System.out.println("you don't have enough money");
				conn.rollback();
			} else {
				String findReceiver = "select * from account_day where name = ?";
				pstmt = conn.prepareStatement(findReceiver);
				pstmt.setString(1, receiver);
				receiverRs = pstmt.executeQuery();
				
				if(receiverRs != null){
					receiverRs.next();
					int moneyReceiver = receiverRs.getInt("balance")+ money;
					int moneySender = rs.getInt("balance")-money;
					System.out.println(moneyReceiver);
					System.out.println(moneySender);
					
					String updateReceiver = "update account_day set balance =? where name = ?";
					pstmt = conn.prepareStatement(updateReceiver);
					pstmt.setInt(1, moneyReceiver);
					pstmt.setString(2, receiverRs.getString("name"));
					pstmt.execute();

					
					String updateSender = "update account_day set balance =? where name =?";
					pstmt = conn.prepareStatement(updateSender);
					pstmt.setInt(1, moneySender);
					pstmt.setString(2, rs.getString("name"));
					pstmt.execute();

					conn.commit();
					
				}else {
					System.out.println("no receiver");
					conn.rollback();
				}
			}
			
			
			
			// "select count(*) from account_day where name = ?";
			// 했을 때 0이면 rollback;
		} catch (Exception e) {
			System.out.println("Transfer error");
			e.printStackTrace();
		}
	}
}
