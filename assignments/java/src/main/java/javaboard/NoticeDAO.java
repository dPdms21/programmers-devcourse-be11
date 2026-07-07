package javaboard;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class NoticeDAO {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    private Connection getConnection() {
        String url = dotenv.get("DB_URL");
        String username = dotenv.get("DB_USERNAME");
        String password = dotenv.get("DB_PASSWORD");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, username, password);
        }
        catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkUserId(String userId) {
        String sql = "select count(*) from user where user_id = ?";

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setString(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

            return false;
        }
        catch (SQLException e) {
            throw new RuntimeException("아이디 중복 확인 중 오류 발생", e);
        }
    }

    public boolean signupExc(String userId, String password, String name) {
        String sql = "insert into user (user_id, password, name) values (?, ?, ?)";

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, userId);
            ps.setString(2, password);
            ps.setString(3, name);

            return ps.executeUpdate() > 0;
        }
        catch (SQLException e) {
            throw new RuntimeException("회원가입 중 오류 발생", e);
        }
    }

    public SignInResponseDTO signInExc(String userId, String password) {
        String sql = "select user_id, password, name from user where user_id = ?";

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                String dbPassword = rs.getString("password");
                String name = rs.getString("name");

                if (!dbPassword.equals(password)) {
                    return new SignInResponseDTO(false, null, null);
                }

                return new SignInResponseDTO(true, userId, name);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("로그인 중 오류 발생", e);
        }
    }

    public boolean newNotice(String userId, String content) {
        String sql = "INSERT INTO content (user_id, content) VALUES (?, ?)";

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, userId);
            ps.setString(2, content);

            return ps.executeUpdate() > 0;
        }
        catch (SQLException e) {
            throw new RuntimeException("글 등록 중 오류 발생", e);
        }
    }

    public List<ContentDTO> getList() {
        String sql = "SELECT id, user_id, content, created FROM content ORDER BY id DESC";
        List<ContentDTO> list = new ArrayList<>();

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String userId = rs.getString("user_id");
                String content = rs.getString("content");
                String created = rs.getTimestamp("created")
                        .toLocalDateTime()
                        .format(FORMATTER);

                list.add(new ContentDTO(id, userId, content, created));
            }

            return list;
        }
        catch (SQLException e) {
            throw new RuntimeException("글 목록 조회 중 오류 발생", e);
        }
    }

    public List<ContentDTO> getListByUserId(String userId) {
        String sql = "SELECT id, user_id, content, created FROM content WHERE user_id = ? ORDER BY id DESC";
        List<ContentDTO> list = new ArrayList<>();

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String writer = rs.getString("user_id");
                    String content = rs.getString("content");
                    String created = rs.getTimestamp("created")
                            .toLocalDateTime()
                            .format(FORMATTER);

                    list.add(new ContentDTO(id, writer, content, created));
                }
            }

            return list;
        }
        catch (SQLException e) {
            throw new RuntimeException("사용자 글 목록 조회 중 오류 발생", e);
        }
    }

    public boolean updateNotice(int id, String userId, String content) {
        String sql = "UPDATE content SET content = ?, created = ? WHERE id = ? AND user_id = ?";

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, content);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(3, id);
            ps.setString(4, userId);

            return ps.executeUpdate() > 0;
        }
        catch (SQLException e) {
            throw new RuntimeException("글 수정 중 오류 발생", e);
        }
    }

    public boolean deleteNotice(int id, String userId) {
        String sql = "DELETE FROM content WHERE id = ? AND user_id = ?";

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            ps.setString(2, userId);

            return ps.executeUpdate() == 1;
        }
        catch (SQLException e) {
            throw new RuntimeException("글 삭제 중 오류 발생", e);
        }
    }

    public boolean leaveExc(String userId) {
        String sql = "DELETE FROM user WHERE user_id = ?";

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, userId);

            return ps.executeUpdate() > 0;
        }
        catch (SQLException e) {
            throw new RuntimeException("회원 탈퇴 중 오류 발생", e);
        }
    }

    public void deleteContentExc(String userId) {
        String sql = "DELETE FROM content WHERE user_id = ?";

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, userId);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException("회원 글 삭제 중 오류 발생", e);
        }
    }

    public boolean leaveWithCascade(String userId) {
        String sql = "DELETE FROM user WHERE user_id = ?";

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, userId);

            return ps.executeUpdate() > 0;
        }
        catch (SQLException e) {
            throw new RuntimeException("회원 탈퇴 중 오류 발생", e);
        }
    }

    public List<ContentDTO> searchNotice(String keyword) {
        String sql = "SELECT id, user_id, content, created FROM content WHERE content LIKE ? ORDER BY id DESC";
        List<ContentDTO> list = new ArrayList<>();

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String userId = rs.getString("user_id");
                    String content = rs.getString("content");
                    String created = rs.getTimestamp("created")
                            .toLocalDateTime()
                            .format(FORMATTER);

                    list.add(new ContentDTO(id, userId, content, created));
                }
            }

            return list;
        }
        catch (SQLException e) {
            throw new RuntimeException("글 검색 중 오류 발생", e);
        }
    }

    public List<ContentDTO> getListByPage(int page, int size) {
        String sql = "SELECT id, user_id, content, created FROM content ORDER BY id DESC LIMIT ? OFFSET ?";
        List<ContentDTO> list = new ArrayList<>();

        int offset = (page - 1) * size;

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, size);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String userId = rs.getString("user_id");
                    String content = rs.getString("content");
                    String created = rs.getTimestamp("created")
                            .toLocalDateTime()
                            .format(FORMATTER);

                    list.add(new ContentDTO(id, userId, content, created));
                }
            }

            return list;
        }
        catch (SQLException e) {
            throw new RuntimeException("글 목록 페이징 조회 중 오류 발생", e);
        }
    }

    public int getTotalPages(int size) {
        String sql = "SELECT count(*) FROM content";

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            if (rs.next()) {
                int totalCount = rs.getInt(1);
                return (int) Math.ceil((double) totalCount / size);
            }

            return 0;
        }
        catch (SQLException e) {
            throw new RuntimeException("전체 페이지 수 조회 중 오류 발생", e);
        }
    }
}
