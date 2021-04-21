package service;

import model.ContactModel;
import model.InputListModel;
import utils.Db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class ContactService {
    private Db DbUtil;
    private String dbTable = "contact";

    public String getDbTable() {
        return dbTable;
    }

    public ContactModel find(ContactModel contact) {
        Connection conn = DbUtil.getConn();
        PreparedStatement pstmt = null;

        var select = "select id, owner_email, dst_obj, cate, memo, create_at, delete_timestamp from";
        // 拼接sql
        var sql = String.join(" ", select, dbTable, where(contact), "limit 1");

        var output = new ContactModel();
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt = pstmtSetParam(pstmt, contact);
            ResultSet result = pstmt.executeQuery();
            if (result.next()) {
                output.setId(result.getInt("id"));
                output.setOwnerEmail(result.getString("owner_email"));
                output.setDstObj(result.getString("dst_obj"));
                output.setCate(result.getInt("cate"));
                output.setMemo(result.getString("memo"));
                output.setCreateAt(result.getString("create_at"));
                output.setDeleteTimestamp(result.getInt("delete_timestamp"));
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

    public List<ContactModel> findAll(ContactModel contact) {
        Connection conn = DbUtil.getConn();
        PreparedStatement pstmt = null;

        var select = "select id, owner_email, dst_obj, cate, memo, create_at, delete_timestamp from";
        // 拼接sql
        var sql = String.join(" ", select, dbTable, where(contact));
        var output = new ArrayList<ContactModel>();
        try {
            pstmt = conn.prepareStatement(sql);

            pstmt = pstmtSetParam(pstmt, contact);

            var result = pstmt.executeQuery();

            while (result.next()) {
                var item = new ContactModel();
                item.setId(result.getInt("id"));
                item.setOwnerEmail(result.getString("owner_email"));
                item.setDstObj(result.getString("dst_obj"));
                item.setCate(result.getInt("cate"));
                item.setMemo(result.getString("memo"));
                item.setCreateAt(result.getString("create_at"));
                item.setDeleteTimestamp(result.getInt("delete_timestamp"));

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

    // 通过群id查找群成员
    public List<ContactModel> findCommunity(String communityId) {
        var con = new ContactModel();
        con.setDstObj(communityId);
        con.setCate(ContactModel.CONCAT_CATE_COMUNITY);

        return findAll(con);
    }

    public ContactModel findList(ContactModel contact, InputListModel input) {
        Connection conn = DbUtil.getConn();
        PreparedStatement pstmt;

        var select = "select id, owner_email, dst_obj, cate, memo, create_at, delete_timestamp from";
        var page = "LIMIT 10 OFFSET 0";
        // 拼接sql
        var sql = String.join(" ", select, dbTable, where(contact));

        var output = new ContactModel();
        try {
            pstmt = conn.prepareStatement(sql);

            pstmt = pstmtSetParam(pstmt, contact);

            ResultSet result = pstmt.executeQuery();

            if (result.next()) {
                output.setId(result.getInt("id"));
                output.setOwnerEmail(result.getString("owner_email"));
                output.setDstObj(result.getString("dst_obj"));
                output.setCate(result.getInt("cate"));
                output.setMemo(result.getString("memo"));
                output.setCreateAt(result.getString("create_at"));
                output.setDeleteTimestamp(result.getInt("delete_timestamp"));
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

    public boolean insert(ContactModel contact) {
        return false;
    }

    public boolean insert(ContactModel contact, Connection conn) {
        PreparedStatement pstmt = null;
        var sql = String.join(" ", "insert into", dbTable, "(owner_email, dst_obj, cate, memo)", "values(?, ?, ?, ?)");

        var isOk = false;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, contact.getOwnerEmail());
            pstmt.setString(2, contact.getDstObj());
            pstmt.setInt(3, contact.getCate());
            pstmt.setString(4, contact.getMemo());
            isOk = pstmt.executeUpdate() > 0;

            pstmt.close();
//            conn.close();
        } catch(SQLException se){
            se.printStackTrace();
        } finally {
//            try{
//                if (conn != null) conn.close();
//            } catch (SQLException se) {
//                se.printStackTrace();
//            }
        }



        return isOk;
    }

    public boolean addFriend(ContactModel contact) {
        Connection conn = DbUtil.getConn();

        var c1 = false;
        var c2 = false;
        try {
            // 取消一下自动提交事务
            conn.setAutoCommit(false);

            // "我添加对方好友"
            c1 = insert(contact, conn);
            // "对方添加我好友"
            var ownerEmail = contact.getOwnerEmail();
            contact.setOwnerEmail(contact.getDstObj());
            contact.setDstObj(ownerEmail);
            c2 = insert(contact, conn);

            if (c1 && c2) {
                // 提交事务
                conn.commit();
            } else {
                // 回滚
                conn.rollback();
            }

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


        return c1 && c2;
    }

    private String where(ContactModel contact, boolean ...normals) {
        var normal = normals.length > 0 ? normals[0] : true;

        StringJoiner where = new StringJoiner(" and ");

        if (contact.getId() > 0) {
            where.add("id = ?");
        }
        if (contact.getOwnerEmail() != null) {
            where.add("owner_email = ?");
        }
        if (contact.getDstObj() != null) {
            where.add("dst_obj = ?");
        }
        if (contact.getCate() != 0) {
            where.add("cate = ?");
        }
        if (contact.getMemo() != null) {
            where.add("memo = ?");
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

    private PreparedStatement pstmtSetParam(PreparedStatement pstmt, ContactModel contact) throws SQLException {
        int paraIndex = 1;
        if (contact.getId() > 0) {
            pstmt.setInt(paraIndex++, contact.getId());
        }
        if (contact.getOwnerEmail() != null) {
            pstmt.setString(paraIndex++, contact.getOwnerEmail());
        }

        if (contact.getDstObj() != null) {
            pstmt.setString(paraIndex++, contact.getDstObj());
        }

        if (contact.getCate() != 0) {
            pstmt.setInt(paraIndex++, contact.getCate());
        }

        if (contact.getMemo() != null) {
            pstmt.setString(paraIndex++, contact.getMemo());
        }

        if (contact.getCreateAt() != null) {
            pstmt.setString(paraIndex++, contact.getCreateAt());
        }

        return pstmt;
    }
}
