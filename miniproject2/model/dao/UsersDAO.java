package model.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import model.dto.UsersDTO;

// 싱글톤 패턴
public class UsersDAO extends BaseDAO {

    private UsersDAO() {}
    private static final UsersDAO instance = new UsersDAO();
    public static UsersDAO getInstance() { return instance; }


    // =========================================================
    // [1] 회원가입
    // =========================================================
    public boolean signup(UsersDTO dto) {

        String sql = "INSERT INTO Users (u_student_id, u_pwd, u_name, u_phone) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dto.getU_student_id());
            ps.setString(2, dto.getU_pwd());
            ps.setString(3, dto.getU_name());
            ps.setString(4, dto.getU_phone());

            int rows = ps.executeUpdate();
            return rows == 1;

        } catch(Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    // =========================================================
    // [2] 로그인
    // =========================================================
    public UsersDTO login(String studentId, String pwd) {

        String sql = "SELECT * FROM Users WHERE u_student_id = ? AND u_pwd = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, studentId);
            ps.setString(2, pwd);

            try (ResultSet rs = ps.executeQuery()) {

                if(rs.next()) {

                    return new UsersDTO(
                        rs.getInt("u_no"),
                        rs.getString("u_student_id"),
                        rs.getString("u_pwd"),
                        rs.getString("u_name"),
                        rs.getString("u_phone"),
                        rs.getString("u_grade")
                    );
                }
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    // =========================================================
    // [3] 학번 유효성 검사
    // true  = 이미 존재하는 학번
    // false = 존재하지 않는 학번
    // =========================================================
    public boolean checkStudentId(String studentId) {

        String sql = "SELECT * FROM Users WHERE u_student_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    // =========================================================
    // [4] 연락처 유효성 검사
    // true  = 사용 가능한 연락처
    // false = 이미 존재하는 연락처
    // =========================================================
    public boolean checkPhone(String phone) {

        String sql = "SELECT * FROM Users WHERE u_phone = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, phone);

            try (ResultSet rs = ps.executeQuery()) {

                if(rs.next()) {
                    return false;
                }
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        return true;
    }


    // =========================================================
    // [5] 전체 회원 조회 (관리자용)
    // =========================================================
    public ArrayList<UsersDTO> selectAllUsers() {

        ArrayList<UsersDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM Users";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while(rs.next()) {

                UsersDTO user = new UsersDTO(
                    rs.getInt("u_no"),
                    rs.getString("u_student_id"),
                    rs.getString("u_pwd"),
                    rs.getString("u_name"),
                    rs.getString("u_phone"),
                    rs.getString("u_grade")
                );

                list.add(user);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        return list;
    }

} // class end