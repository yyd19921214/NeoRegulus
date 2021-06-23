import java.sql.*;

public class JdbcTest {

    static final String JDBC_DRIVER = "com.example.yudyang.regulus.jdbc.driver.ElasticDriver";
    static final String DB_URL = "jdbc:es://localhost:9200/yudyang_test_index?useSSL=false&mode=single";

    static final String USER = "admin";
    static final String PASS = "admin";

    private static void conn1(){
        Connection conn = null;
        Statement stmt = null;
        try{
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            stmt = conn.createStatement();
            String sql;
            sql = "SELECT * FROM yudyang_test_index where name = yudyang limit 10";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                String id  = rs.getString("_id");
                System.out.println(id);
//                String name = rs.getString("name");
//                String url = rs.getString("list");
//                System.out.print("ID: " + id);
//                System.out.print(", 站点名称: " + name);
//                System.out.print(", 站点 URL: " + url);
//                System.out.print(id+ "\n");
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch(Exception se){
            se.printStackTrace();
        }
        finally{
            try{
                if(stmt!=null) stmt.close();
            }catch(SQLException ignored){
            }
            try{
                if(conn!=null) conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        conn1();
    }

}
