package part4;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;

public class A_jdbc {
    private static final Dotenv dotenv = Dotenv.load();

    public Connection connection() {
        String url = dotenv.get("DB_URL");
        String user = dotenv.get("DB_USERNAME");
        String password = dotenv.get("DB_PASSWORD");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);
            System.out.println("Conn Sucess!");

            return connection;
        }
        catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertData (String name, int age, String phone) {
        String query = "insert into member(name,age,phone) values (?,?,?)";

        try (
                Connection conn = connection();
                PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, phone);

            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void selectAll() {
        String query = "SELECT id, name, age, phone FROM member";

        try (
                Connection conn = connection();
                PreparedStatement ps = conn.prepareStatement( query );
        ) {
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String phone = resultSet.getString("phone");

                System.out.println( id + " " + name + " " + age + " " + phone );
                System.out.println("==========");
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void selectOne (int id) {
        String query = "SELECT id, name, age, phone FROM member WHERE id = ?";

        try (
                Connection conn = connection();
                PreparedStatement ps = conn.prepareStatement( query );
        ) {
            ps.setInt(1, id);

            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                int id2 = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String phone = resultSet.getString("phone");

                System.out.println( id2 + " : " + name + " : " + age + " : " + phone );
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateData( int id, String name, int age, String phone ) {
        String query = "UPDATE member SET name = ?, age = ?, phone = ? WHERE id = ?";

        try (
                Connection conn = connection();
                PreparedStatement ps = conn.prepareStatement(query);
        ) {
            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, phone);
            ps.setInt(4, id);

            int result = ps.executeUpdate();

            if ( result > 0 ) {
                System.out.println("update success!");
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteData(int id) {
        String query = "DELETE FROM member WHERE id = ?";

        try (
                Connection conn = connection();
                PreparedStatement ps = conn.prepareStatement(query);
        ) {
            ps.setInt(1, id);
            int result = ps.executeUpdate();

            if ( result > 0 ) {
                System.out.println("delete success!");
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        A_jdbc aJdbc = new A_jdbc();
//        aJdbc.insertData("홍길순", 21, "010-1234-5678");
//        aJdbc.selectAll();
//        aJdbc.selectOne(1);
//        aJdbc.updateData(2, "홍홍홍", 30, "010-3232-4545");
//        aJdbc.deleteData(2);
    }
}
