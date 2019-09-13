package jdbcTest;
import java.util.Scanner;
import java.sql.*;

public class Test {
static String CarNo;
static boolean paid = false;

	public static void main(String[] args) {
		System.out.println("Stand By");
		
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			
			Class.forName("com.mysql.jdbc.Driver");
			
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/graduationproject?autoReconnect=true&useSSL=false","root","tnrns8256");
		
		
			if(isCar()) {
				CarNo = Carno();
				System.out.println("Open Gate");
				
				//jdbc를 이용해 DB에 입차 정보 입력
				String sql = "insert into carin values(?,?,?)"; 
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1,CarNo);
				pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
				pstmt.setString(3, "NotPaid");
				pstmt.executeUpdate();	
				
				System.out.println("Car In");
				System.out.println("Close Gate");
			}
		
		System.out.println("Stand By");
		
		System.out.println("조회하실 차 번호를 입력하세요");
		Scanner CarNum = new Scanner(System.in);
		
		String input = CarNum.nextLine();
		
		
		//결제가 완료되었는지 확인
		String sql = "select IsPaid from carin where CarNo = ";
		sql = sql + input;
		
		pstmt = conn.prepareStatement(sql);
		
		
		ResultSet isPaid = pstmt.executeQuery();
		while(isPaid.next()) {
			//if(isPaid.getString(1) == "Paid")
				//paid = true;
		
			System.out.println(isPaid.getString(1));
			String a = "Paid";
			if(isPaid.getString(1).equals(a)) {
				System.out.println("Open Gate");				
				System.out.println("Car Out");
				System.out.println("Close Gate");
			}
		}
		
		
			
		System.out.println("Stand By");
		
		} catch (ClassNotFoundException | SQLException e) {
			
			e.printStackTrace();
			
		}finally{
			
			if(pstmt != null) try{pstmt.close();}catch(SQLException sqle){}
			if(conn != null) try{conn.close();}catch(SQLException sqle){}
			
		}
	}
	
	
	public static boolean isCar() {
		Scanner key = new Scanner(System.in);
		int input;
		
		do { input = key.findInLine(".").charAt(0);
        	if (input == '1') {
        	return true;	
        	}else return false;
		} while (input != 0);
	}
	
	public static String Carno() {
		System.out.println("자동차 번호를 입력하세요.");
		
		Scanner carno = new Scanner(System.in);
		String CarNo;
		
		do {CarNo = carno.nextLine();
			return CarNo;
		}while(CarNo != null);
	}
}
