package model.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.dto.EquipmentDTO;
import model.dto.LockerDTO;

public class LockerDAO extends BaseDAO {

    // 싱글톤
    private LockerDAO() {}
    private static final LockerDAO instance = new LockerDAO();
    public static LockerDAO getInstance() { return instance; }


    // =========================================================
    // [1] 보관함 번호 유효성 검사
    // =========================================================
    public boolean l_NoCheck(int l_No) {
        String sql = "SELECT l_no FROM Locker WHERE l_no = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, l_No);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.out.println("보관함 번호 검사 실패 : " + e);
        }
        return false;
    }


    // =========================================================
    // [2] 일반 보관함 등록
    // AUTO_INCREMENT를 이용하여 보관함 번호 자동 생성
    // =========================================================
    public boolean l_add(LockerDTO lockerDTO) {
        String sql = "INSERT INTO Locker (l_location, l_status) VALUES (?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, lockerDTO.getL_Location());
            pstmt.setString(2, lockerDTO.getL_Status());

            int result = pstmt.executeUpdate();
            return result == 1;

        } catch (SQLException e) {
            System.out.println("보관함 등록 실패 : " + e);
        }
        return false;
    }


    // =========================================================
    // [3] 보관함 번호를 지정하여 등록
    // 장비 등록 중 존재하지 않는 보관함을 생성할 때 사용
    // =========================================================
    public boolean l_addWithNo(LockerDTO lockerDTO) {
        String sql = "INSERT INTO Locker (l_no, l_location, l_status) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, lockerDTO.getL_No());
            pstmt.setString(2, lockerDTO.getL_Location());
            pstmt.setString(3, lockerDTO.getL_Status());

            int result = pstmt.executeUpdate();
            return result == 1;

        } catch (Exception e) {
            System.out.println("보관함 등록 실패 : " + e);
        }
        return false;
    }


    // =========================================================
    // [4] 전체 보관함 조회
    // =========================================================
    public ArrayList<LockerDTO> l_findAll() {
        ArrayList<LockerDTO> list = new ArrayList<>();
        String sql = "SELECT l_no, l_location, l_status FROM Locker ORDER BY l_no";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                LockerDTO lockerDTO = new LockerDTO(
                    rs.getInt("l_no"),
                    rs.getString("l_location"),
                    rs.getString("l_status")
                );

                list.add(lockerDTO);
            }

        } catch (SQLException e) {
            System.out.println("전체 보관함 조회 실패 : " + e);
        }
        return list;
    }


    // =========================================================
    // [5] 보관함 개별 조회
    // =========================================================
    public LockerDTO l_find(int l_No) {
        String sql = "SELECT l_no, l_location, l_status FROM Locker WHERE l_no = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, l_No);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    return new LockerDTO(
                        rs.getInt("l_no"),
                        rs.getString("l_location"),
                        rs.getString("l_status")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("보관함 개별 조회 실패 : " + e);
        }
        return null;
    }


    // =========================================================
    // [6] 보관함 수정
    // =========================================================
    public boolean l_update(LockerDTO lockerDTO) {
        String sql = "UPDATE Locker SET l_location = ?, l_status = ? WHERE l_no = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, lockerDTO.getL_Location());
            pstmt.setString(2, lockerDTO.getL_Status());
            pstmt.setInt(3, lockerDTO.getL_No());

            int result = pstmt.executeUpdate();
            return result == 1;

        } catch (SQLException e) {
            System.out.println("보관함 수정 실패 : " + e);
        }
        return false;
    }


    // =========================================================
    // [7] 보관함 삭제
    // =========================================================
    public boolean l_delete(int l_No) {
        String sql = "DELETE FROM Locker WHERE l_no = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, l_No);

            int result = pstmt.executeUpdate();
            return result == 1;

        } catch (SQLException e) {
            System.out.println("보관함 삭제 실패 : " + e);
        }
        return false;
    }


    // =========================================================
    // [8] 보관함 상태 변경
    // =========================================================
    public boolean l_statusupdate(int l_No, String l_Status) {
        String sql = "UPDATE Locker SET l_status = ? WHERE l_no = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, l_Status);
            pstmt.setInt(2, l_No);

            int result = pstmt.executeUpdate();
            return result == 1;

        } catch (SQLException e) {
            System.out.println("보관함 상태 변경 실패 : " + e);
        }
        return false;
    }


    // =========================================================
    // [9] 보관함에 등록된 장비 조회
    // =========================================================
    public EquipmentDTO l_equipmentfind(int l_No) {
        String sql = "SELECT * FROM Equipment WHERE l_no = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, l_No);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    return new EquipmentDTO(
                        rs.getInt("e_no"),
                        rs.getString("e_name"),
                        rs.getString("e_category"),
                        rs.getString("e_status"),
                        rs.getInt("l_no")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("보관함 장비 조회 실패 : " + e);
        }
        return null;
    }
}