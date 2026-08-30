package model.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.dto.EquipmentDTO;

public class EquipmentDAO extends BaseDAO {

    // 싱글톤
    private EquipmentDAO() {}
    private static final EquipmentDAO instance = new EquipmentDAO();
    public static EquipmentDAO getInstance() { return instance; }


    // =========================================================
    // [1] 장비번호 유효성 검사
    // =========================================================
    public boolean e_NoCheck(int e_No) {
        String sql = "SELECT e_no FROM Equipment WHERE e_no = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, e_No);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.out.println("장비 유효성 검사 실패 : " + e);
        }
        return false;
    }


    // =========================================================
    // [2] 전체 장비 조회
    // =========================================================
    public ArrayList<EquipmentDTO> e_findAll() {
        ArrayList<EquipmentDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM Equipment";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                EquipmentDTO equipmentDTO = new EquipmentDTO(
                    rs.getInt("e_no"),
                    rs.getString("e_name"),
                    rs.getString("e_category"),
                    rs.getString("e_status"),
                    rs.getInt("l_no")
                );

                list.add(equipmentDTO);
            }

        } catch (SQLException e) {
            System.out.println("전체 장비 조회 실패 : " + e);
        }
        return list;
    }


    // =========================================================
    // [3] 장비 상세 조회
    // =========================================================
    public EquipmentDTO e_find(int e_No) {
        String sql = "SELECT * FROM Equipment WHERE e_no = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, e_No);

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
            System.out.println("장비 상세 조회 실패 : " + e);
        }
        return null;
    }


    // =========================================================
    // [4] 장비 카테고리별 조회
    // =========================================================
   public ArrayList<EquipmentDTO> e_categoryfind(String e_Category) {
    ArrayList<EquipmentDTO> list = new ArrayList<>();
    String sql = "SELECT * FROM Equipment WHERE TRIM(e_category) = ?";

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, e_Category.trim());

        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                EquipmentDTO equipmentDTO = new EquipmentDTO(
                    rs.getInt("e_no"),
                    rs.getString("e_name"),
                    rs.getString("e_category"),
                    rs.getString("e_status"),
                    rs.getInt("l_no")
                );
                list.add(equipmentDTO);
            }
        }
    } catch (SQLException e) {
        System.out.println("카테고리별 장비 조회 실패 : " + e);
    }
    return list;
}


    // =========================================================
    // [5] 대여가능 장비 조회
    // =========================================================
    public ArrayList<EquipmentDTO> e_available() {
        ArrayList<EquipmentDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM Equipment WHERE e_status = '대여가능'";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                EquipmentDTO equipmentDTO = new EquipmentDTO(
                    rs.getInt("e_no"),
                    rs.getString("e_name"),
                    rs.getString("e_category"),
                    rs.getString("e_status"),
                    rs.getInt("l_no")
                );

                list.add(equipmentDTO);
            }

        } catch (SQLException e) {
            System.out.println("대여가능 장비 조회 실패 : " + e);
        }
        return list;
    }


    // =========================================================
    // [6] 보관함 사용 여부 확인
    // 장비 등록 시 사용
    // true  = 이미 다른 장비가 사용중
    // false = 사용 가능한 보관함
    // =========================================================
    public boolean lockerUseCheck(int l_No) {
        String sql = "SELECT e_no FROM Equipment WHERE l_no = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, l_No);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.out.println("보관함 중복 검사 실패 : " + e);
        }
        return false;
    }


    // =========================================================
    // [7] 장비 수정 시 보관함 중복 검사
    // 현재 수정중인 장비는 검사 대상에서 제외
    // =========================================================
    public boolean lockerUseCheck(int l_No, int e_No) {
        String sql = "SELECT e_no FROM Equipment WHERE l_no = ? AND e_no != ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, l_No);
            pstmt.setInt(2, e_No);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.out.println("보관함 중복 검사 실패 : " + e);
        }
        return false;
    }


    // =========================================================
    // [8] 장비 등록
    // =========================================================
    public boolean e_add(EquipmentDTO equipmentDTO) {

        if (lockerUseCheck(equipmentDTO.getL_NO())) {
            System.out.println("[안내] 이미 다른 장비가 사용중인 보관함입니다.");
            return false;
        }

        String sql = "INSERT INTO Equipment (e_name, e_category, e_status, l_no) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, equipmentDTO.getE_Name());
            pstmt.setString(2, equipmentDTO.getE_Category());
            pstmt.setString(3, equipmentDTO.getE_Status());
            pstmt.setInt(4, equipmentDTO.getL_NO());

            int result = pstmt.executeUpdate();
            return result == 1;

        } catch (SQLException e) {
            System.out.println("장비 등록 실패 : " + e);
        }
        return false;
    }


    // =========================================================
    // [9] 장비 정보 수정
    // =========================================================
    public boolean e_update(EquipmentDTO equipmentDTO) {

        if (lockerUseCheck(equipmentDTO.getL_NO(), equipmentDTO.getE_No())) {
            System.out.println("[안내] 해당 보관함은 다른 장비가 사용중입니다.");
            return false;
        }

        String sql = "UPDATE Equipment SET e_name = ?, e_category = ?, e_status = ?, l_no = ? WHERE e_no = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, equipmentDTO.getE_Name());
            pstmt.setString(2, equipmentDTO.getE_Category());
            pstmt.setString(3, equipmentDTO.getE_Status());
            pstmt.setInt(4, equipmentDTO.getL_NO());
            pstmt.setInt(5, equipmentDTO.getE_No());

            int result = pstmt.executeUpdate();
            return result == 1;

        } catch (SQLException e) {
            System.out.println("장비 정보 수정 실패 : " + e);
        }
        return false;
    }


    // =========================================================
    // [10] 장비 상태 변경
    // =========================================================
    public boolean e_statusupdate(int e_No, String e_Status) {
        String sql = "UPDATE Equipment SET e_status = ? WHERE e_no = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, e_Status);
            pstmt.setInt(2, e_No);

            int result = pstmt.executeUpdate();
            return result == 1;

        } catch (SQLException e) {
            System.out.println("장비 상태 변경 실패 : " + e);
        }
        return false;
    }


    // =========================================================
    // [11] 장비 보관함 변경
    // =========================================================
    public boolean e_lockerupdate(int e_No, int l_No) {

        if (lockerUseCheck(l_No, e_No)) {
            System.out.println("[안내] 해당 보관함은 다른 장비가 사용중입니다.");
            return false;
        }

        String sql = "UPDATE Equipment SET l_no = ? WHERE e_no = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, l_No);
            pstmt.setInt(2, e_No);

            int result = pstmt.executeUpdate();
            return result == 1;

        } catch (SQLException e) {
            System.out.println("장비 보관함 변경 실패 : " + e);
        }
        return false;
    }


    // =========================================================
    // [12] 장비 대여이력 확인
    // true  = 대여이력 있음
    // false = 대여이력 없음
    // =========================================================
    public boolean rentalHistoryCheck(int e_No) {
        String sql = "SELECT r_no FROM Rental WHERE e_no = ? LIMIT 1";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, e_No);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.out.println("장비 대여이력 확인 실패 : " + e);
        }
        return false;
    }


    // =========================================================
    // [13] 장비 삭제
    // =========================================================
    public boolean e_delete(int e_No) {

        if (!e_NoCheck(e_No)) {
            System.out.println("[안내] 존재하지 않는 장비번호입니다.");
            return false;
        }

        if (rentalHistoryCheck(e_No)) {
            System.out.println("[안내] 해당 장비는 대여 이력이 존재하여 삭제할 수 없습니다.");
            return false;
        }

        String sql = "DELETE FROM Equipment WHERE e_no = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, e_No);

            int result = pstmt.executeUpdate();
            return result == 1;

        } catch (SQLException e) {
            System.out.println("장비 삭제 실패 : " + e);
        }
        return false;
    }
}