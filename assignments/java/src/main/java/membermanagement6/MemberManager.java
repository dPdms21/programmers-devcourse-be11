package membermanagement6;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;

public class MemberManager {
    private static final Dotenv dotenv = Dotenv.load();
    private final int capacity;

    public MemberManager(int capacity) {
        this.capacity = capacity;
    }

    private Connection connection() {
        String url = dotenv.get("DB_URL");
        String user = dotenv.get("DB_USERNAME");
        String password = dotenv.get("DB_PASSWORD");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            return DriverManager.getConnection(url, user, password);
        }
        catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int capacity() {
        return capacity;
    }

    public void add(Member m) {
        String sql = "INSERT INTO member (grade, name, email, phone) VALUES (?, ?, ?, ?)";

        try (
                Connection conn = connection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, m.getGrade());
            ps.setString(2, m.getName());
            ps.setString(3, m.getEmail());
            ps.setString(4, m.getPhone());
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Member toMember(ResultSet rs) throws SQLException {
        String grade = rs.getString("grade");
        String name  = rs.getString("name");
        String email = rs.getString("email");
        String phone = rs.getString("phone");

        return grade.equals("VIP")
                ? new VipMember(name, email, phone)
                : new NormalMember(name, email, phone);
    }

    public Member findByEmail(String email) {
        String sql = "SELECT * FROM member WHERE email = ?";

        try (
                Connection conn = connection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return toMember(rs);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public Member findByName(String name) {
        String sql = "SELECT * FROM member WHERE name = ?";

        try (
                Connection conn = connection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return toMember(rs);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public void printAll() {
        String sql = "SELECT * FROM member";

        try (
                Connection conn = connection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            boolean empty = true;

            while (rs.next()) {
                toMember(rs).printInfo();
                empty = false;
            }

            if (empty) {
                System.out.println("등록된 회원 없음");
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean existsEmail(String email) {
        String sql = "SELECT COUNT(*) FROM member WHERE email = ?";

        try (
                Connection conn = connection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    public int size() {
        String sql = "SELECT COUNT(*) FROM member";

        try (
                Connection conn = connection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return 0;
    }

    public boolean isFull() {
        return size() >= capacity;
    }

    public boolean update(String email, String name, String newEmail, String phone) {
        String sql = "UPDATE member SET name = ?, email = ?, phone = ? WHERE email = ?";

        try (
                Connection conn = connection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, name);
            ps.setString(2, newEmail);
            ps.setString(3, phone);
            ps.setString(4, email);

            return ps.executeUpdate() > 0;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean delete(String email) {
        String sql = "DELETE FROM member WHERE email = ?";

        try (
                Connection conn = connection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, email);

            return ps.executeUpdate() > 0;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
