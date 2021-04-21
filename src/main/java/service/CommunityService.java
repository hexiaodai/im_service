package service;

import model.CommunityModel;
import model.ContactModel;
import utils.Db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class CommunityService {
    private Db DbUtil;
    private String dbTable = "community";

    public CommunityModel find(CommunityModel comm) {
        Connection conn = DbUtil.getConn();
        PreparedStatement pstmt = null;
        var select = "select id, name, owner_email, icon, memo, create_at, delete_timestamp from";
        // 拼接sql
        var sql = String.join(" ", select, dbTable, where(comm), "limit 1");

        var output = new CommunityModel();
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt = pstmtSetParam(pstmt, comm);
            ResultSet result = pstmt.executeQuery();
            if (result.next()) {
                output.setId(result.getInt("id"));
                output.setName(result.getString("name"));
                output.setOwnerEmail(result.getString("owner_email"));
                output.setIcon(result.getString("icon"));
                output.setMemo(result.getString("memo"));
                output.setCreateAt(result.getLong("create_at"));
                output.setDeleteTimestamp(result.getLong("delete_timestamp"));
            }
            result.close();
            pstmt.close();
            conn.close();
        } catch(SQLException se){
            se.printStackTrace();
        } finally {
            try{
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return output;
    }

    public List<CommunityModel> searchCommunity(String info) {
        Connection conn = DbUtil.getConn();
        PreparedStatement pstmt = null;

        var select = "select id, name, owner_email, icon, memo, create_at, delete_timestamp from";
        var where = "where id like ? or name like ? and delete_timestamp = 0";
        // 拼接sql
        var sql = String.join(" ", select, dbTable, where);
        var output = new ArrayList<CommunityModel>();
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + info + "%");
            pstmt.setString(2, "%" + info + "%");

            ResultSet result = pstmt.executeQuery();

            while (result.next()) {
                var item = new CommunityModel();
                item.setId(result.getInt("id"));
                item.setName(result.getString("name"));
                item.setOwnerEmail(result.getString("owner_email"));
                item.setIcon(result.getString("icon"));
                item.setMemo(result.getString("memo"));
                item.setCreateAt(result.getLong("create_at"));
                item.setDeleteTimestamp(result.getLong("delete_timestamp"));
                output.add(item);
            }
            result.close();
            pstmt.close();
            conn.close();
        } catch(SQLException se){
            se.printStackTrace();
        } finally {
            try{
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

        return output;
    }

    public List<CommunityModel> findAll(CommunityModel comm) {
        Connection conn = DbUtil.getConn();
        PreparedStatement pstmt = null;

        var select = "select id, name, owner_email, icon, memo, create_at, delete_timestamp from";
        var where = "where id like ? or name like ? and delete_timestamp = 0";
        // 拼接sql
        var sql = String.join(" ", select, dbTable, where);
        var output = new ArrayList<CommunityModel>();
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt = pstmtSetParam(pstmt, comm);

            ResultSet result = pstmt.executeQuery();

            while (result.next()) {
                var item = new CommunityModel();
                item.setId(result.getInt("id"));
                item.setName(result.getString("name"));
                item.setOwnerEmail(result.getString("owner_email"));
                item.setIcon(result.getString("icon"));
                item.setMemo(result.getString("memo"));
                item.setCreateAt(result.getLong("create_at"));
                item.setDeleteTimestamp(result.getLong("delete_timestamp"));
                output.add(item);
            }
            result.close();
            pstmt.close();
            conn.close();
        } catch(SQLException se){
            se.printStackTrace();
        } finally {
            try{
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

        return output;
    }

    public List<CommunityModel> findUserCommunityByContact(List<ContactModel> contactList) {
        var output = new ArrayList<CommunityModel>();
        for (ContactModel conItem : contactList) {
            var comm = new CommunityModel();
            comm.setId(Integer.valueOf(conItem.getDstObj()));
            comm = find(comm);
            output.add(comm);
        }

        return output;
    }

    private String where(CommunityModel comm, boolean ...normals) {
        var normal = normals.length > 0 ? normals[0] : true;

        StringJoiner where = new StringJoiner(" and ");

        if (comm.getId() > 0) {
            where.add("id = ?");
        }
        if (comm.getName() != null) {
            where.add("name = ?");
        }
        if (comm.getOwnerEmail() != null) {
            where.add("owner_email = ?");
        }
        if (comm.getIcon() != null) {
            where.add("icon = ?");
        }
        if (comm.getMemo() != null) {
            where.add("memo = ?");
        }
        if (comm.getCreateAt() != 0) {
            where.add("create_at = ?");
        }
        if (normal) {
            where.add("delete_timestamp = 0");
        }

        var result = where.toString();
        if (result != "") {
            result = String.join(" ", "where", where.toString());
        }
        return result;
    }

    private PreparedStatement pstmtSetParam(PreparedStatement pstmt, CommunityModel comm) throws SQLException {
        int paraIndex = 1;
        if (comm.getId() > 0) {
            pstmt.setInt(paraIndex++, comm.getId());
        }
        if (comm.getName() != null) {
            pstmt.setString(paraIndex++, comm.getName());
        }
        if (comm.getOwnerEmail() != null) {
            pstmt.setString(paraIndex++, comm.getOwnerEmail());
        }
        if (comm.getIcon() != null) {
            pstmt.setString(paraIndex++, comm.getIcon());
        }
        if (comm.getMemo() != null) {
            pstmt.setString(paraIndex++, comm.getMemo());
        }
        if (comm.getCreateAt() != 0) {
            pstmt.setLong(paraIndex++, comm.getCreateAt());
        }

        return pstmt;
    }
}
