package dbclass;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Euhzoom {
	private static String url = "jdbc:mysql://localhost/euhzoom";
	private static String user = "day";
	private static String pw = "ekdudrmf2";
	private String gender;

	private int id;
	private static Connection con;

	public static void main(String[] args) {
		Euhzoom euhzoom = new Euhzoom();
		connect();
		 String genderM = euhzoom.getGender();
		 euhzoom.createUser(genderM);
		 euhzoom.getInfo(genderM);
		euhzoom.getIdeal(genderM);

	}

	private void getIdeal(String string) {
		ArrayList<Integer> test = searchIdeal(string);
		ArrayList <String> ttest = makeQuery(test, string); 
		String q = attachString(ttest, string);
		executeIdealQ(q, ttest, string);
			
		
	}

	public static Connection connect() {
		// String url = "jdbc:mysql://localhost:80/euhzoom";
		// String user = "day";
		// String pw = "ekdudrmf2";

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("driver load error:");
			return null;
		}
		System.out.println("driver loading success");
		try {
			con = DriverManager.getConnection(url, user, pw);

		} catch (SQLException e) {
			System.err.println("connect fail:" + e.getMessage());
			return null;
		}
		System.out.println("connection success");
		return con;
	}

	// 여잔지 남잔지 판별
	public String getGender() {
		while (true) {
			System.out.println("당신은 여자인가요? (y/n)");
			Scanner get = new Scanner(System.in);
			String answer = get.next();

			if (answer.equals("y")) {
				return "female";

			} else if (answer.equals("n")) {
				return "male";
			} else {
				System.out.println("제3의 성을 수용하지 않는 꽉 막힌 프로그램입니다.");
				gender = null;
			}
		}
	}

	/**
	 * user의 이름을 받아서 id를 부여하는 작업 나머지 컬럼을 모두 null로
	 * 
	 * @param gender
	 */
	public void createUser(String gender) {
		PreparedStatement pQuery;
		String join = "insert into " + gender + "_user (name) values (?);";
		try {
			pQuery = con.prepareStatement(join);
			System.out.println("당신의 이름은?");
			Scanner name = new Scanner(System.in);
			String n = name.next();
			pQuery.setString(1, n);
			pQuery.execute();
			String getIdQuery = "select id from " + gender
					+ "_user where name =\"" + n + "\";";
			// System.out.println(getIdQuery);
			Statement getId = con.createStatement();
			ResultSet rs = getId.executeQuery(getIdQuery);

			// 사용자의 이름으로 create하고, id를 찾아서 저장해놓음.
			while (rs.next()) {
				id = rs.getInt("id");
			}
			//System.out.println(id);
			pQuery.close();
			getId.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 정보 입력 받는 것. 통합.
	 * 
	 * @param gender
	 */
	public void getInfo(String gender) {
		getOtherInfo("usr", gender);
		getOtherInfo("ideal", gender);
		matchTableInfo("usr", gender);
		matchTableInfo("ideal", gender);
	}

	/**
	 * 나이& 키 를 입력받는 함수
	 * 
	 * @param gender
	 */
	public void getOtherInfo(String target, String gender) {// user ideal

		ArrayList<String> otherInfo = new ArrayList<String>();
		otherInfo.add("age");
		otherInfo.add("height");
		String target_ko;
		try {

			if (target.equals("usr")) {
				target_ko = "당신의 ";
			} else {
				target_ko = "이상형의 ";
			}
			for (int i = 0; i < 2; i++) {
				String varQuery = "update " + gender + "_user SET " + target
						+ "_" + otherInfo.get(i) + "= ? where id = ?;";
				PreparedStatement updateAge;
				System.out.println(target_ko + " " + otherInfo.get(i)
						+ "?? 숫자만 입력");
				Scanner scan = new Scanner(System.in);
				int var = scan.nextInt();
				updateAge = con.prepareStatement(varQuery);
				updateAge.setInt(1, var);
				updateAge.setInt(2, id);
				updateAge.execute();
				updateAge.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 테이블 이용해서 받은 값들을 입력하는 녀석.
	 * 
	 * @param target
	 * @param gender
	 */
	public void matchTableInfo(String target, String gender) {

		ArrayList<String> tableName = new ArrayList<String>();
		tableName.add("personality1");
		tableName.add("personality2");
		tableName.add("residence");
		tableName.add("religion");
		tableName.add("blood");
		tableName.add("job");

		try {
			String table = "";
			String selectQ;
			Statement stmt = con.createStatement();
			PreparedStatement pst;

			String type;
			String target_ko;
			int tableId;

			if (target.equals("usr")) {
				target_ko = "당신의 ";
			} else {
				target_ko = "이상형의 ";
			}
			for (int i = 0; i < 6; i++) {
				selectQ = "select * from " + tableName.get(i) + ";";

				ResultSet record;
				record = stmt.executeQuery(selectQ);
				int count = 0;

				System.out.println(target_ko + tableName.get(i)
						+ "은? 번호를 입력하세요.");
				while (record.next()) {// 테이블 안의 레코드 출력.
					tableId = record.getInt("id");
					type = record.getString("type");
					System.out.println(tableId + ". " + type);
					count++;
				}
				record.close();
				if (!target.equals("usr")) {
					System.out.println((count + 1) + ". 상관없다");
				}

				Scanner scan = new Scanner(System.in);
				int result = scan.nextInt();
				if (result == count + 1 && target.equals("ideal")) {
					continue;
				}
				String query = "update " + gender + "_user set " + target + "_"
						+ tableName.get(i) + " = ? where id = " + id;
				pst = con.prepareStatement(query);
				pst.setInt(1, result);
				pst.execute();
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 사용자의 id로 이상형의 조건을 찾아서  int 배열에 넣어 반환한다. 
	 * @param gender
	 * @return
	 */
	public ArrayList<Integer> searchIdeal(String gender) {
		String ideal_gender = setIdealGender(gender);
		ResultSet result;
		ArrayList<Integer> record = new ArrayList<Integer>();

		try {
			Statement stmt = con.createStatement();
			//int id = 17;// 임시 처리
			String query = "select ideal_age, ideal_height, ideal_personality1, ideal_personality2, ideal_residence, ideal_religion, ideal_job, ideal_blood from "
					+ gender + "_user where id =" + id + ";";
			System.out.println();
			result = stmt.executeQuery(query);
			result.next();
			for (int i = 0; i < 8; i++) {
				record.add(i, result.getInt(i + 1));
			}
			//System.out.println(record);
			result.close();
			return record;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 
	 * @param arrayList
	 * @param gender
	 * @return
	 */
	public ArrayList<String> makeQuery(ArrayList<Integer> arrayList, String gender){
		String ideal_gender = setIdealGender(gender);
		//String query = "select id, name from " + ideal_gender + "_user where ";
		ArrayList<String> makeQueryArray = new ArrayList<String> ();
		ArrayList<String> ColumnList = new ArrayList<String> ();
		ColumnList.add("usr_age");
		ColumnList.add("usr_height");
		ColumnList.add("usr_personality1");
		ColumnList.add("usr_personality2");
		ColumnList.add("usr_residence");
		ColumnList.add("usr_religion");
		ColumnList.add("usr_job");
		ColumnList.add("usr_blood");
				
		
		for(int i = 0; i <arrayList.size(); i++){
			if(arrayList.get(i) !=0){
				String part="";
				if(i == 0){
					part = ColumnList.get(i)+">=" + (arrayList.get(i)-1) + " and " + ColumnList.get(i) + "<="+(arrayList.get(i)+1);
				}else if(i ==1){
					part = ColumnList.get(i)+">=" + (arrayList.get(i)-3) + " and " + ColumnList.get(i) + "<="+(arrayList.get(i)+3);
				} else {
					part = ColumnList.get(i) + " = " + arrayList.get(i) ;
					
				}
				makeQueryArray.add(part);
			}
		}
		//System.out.println(makeQueryArray);
		return makeQueryArray;
	}
	/**
	 * 스트링어레이를 받아서 쿼리구문으로 꿰매서 쿼리구문 완전체 내보냄.
	 * @param makeQueryArray
	 * @param gender
	 * @return
	 */
	public String attachString(ArrayList<String> makeQueryArray, String gender){
		String ideal_gender = setIdealGender(gender);
		String query = "select id, name from " + ideal_gender + "_user where ";
		for(int i = 0; i < makeQueryArray.size(); i++){
			if(i !=0){
				query += " and ";
			}
			query += makeQueryArray.get(i);
			if(i == makeQueryArray.size()-1){
				query += ";";
			}
		}
		//System.out.println(query);
		return query;
		
	}
	
	/**
	 * 최초 쿼리문을 받아서, 스트링 어레이리스트는 쿼리 조건들. 
	 * 모두 붙어있는 최초 쿼리를 실행해보고, 안되면 어레이리스트에 -1 해서 
	 * ATTACHSTRING해서 다시 쿼리 구문을 받아와서 해보고 안되면..반복.
	 * 있으면 이상형 출력.
	 * @param query
	 * @param arrayList
	 * @param gender
	 * @return
	 */
	public ArrayList<String> executeIdealQ(String query, ArrayList<String> arrayList, String gender){
		try {
			Statement stmt = con.createStatement();
			ResultSet ideal = stmt.executeQuery(query);
			int idealId;
			String idealName;
			String q;
			while(!ideal.next()) {
				arrayList.remove(arrayList.size()-1);
				q = attachString(arrayList, gender);
				ideal = stmt.executeQuery(q); 
			}
			do{
				idealId = ideal.getInt("id");
				idealName = ideal.getString("name");
				System.out.println(" 당신의 이상형은 "+idealId+". "+idealName);
			} while(ideal.next());
			stmt.close();
			ideal.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 사용자 성별에 대해서 대상 성별로 세팅해서 돌려보내줌.
	 * @param gender
	 * @return
	 */
	private String setIdealGender(String gender) {
		String ideal_gender;
		
		if (gender.equals("female")) {
			ideal_gender = "male";
		} else {
			ideal_gender = "female";
		}
		return ideal_gender;
	}
}
