package service;

import model.ContactModel;
import model.UserModel;
import utils.Db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class UserService {
    private Db DbUtil;
    private String dbTable = "user";

    public String getDbTable() {
        return dbTable;
    }

    public UserModel find(UserModel user) {
        Connection conn = DbUtil.getConn();
        PreparedStatement pstmt = null;

        var select = "select id, email, salt, uname, avatar, password, create_at, delete_timestamp from";
        // 拼接sql
        var sql = String.join(" ", select, dbTable, where(user), "limit 1");

        var output = new UserModel();
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt = pstmtSetParam(pstmt, user);

            ResultSet result = pstmt.executeQuery();
            if (result.next()) {
                output.setId(result.getInt("id"));
                output.setEmail(result.getString("email"));
                output.setSalt(result.getString("salt"));
                output.setUname(result.getString("uname"));
                output.setAvatar(result.getString("avatar"));
                output.setPassword(result.getString("password"));
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

    public boolean insert(UserModel user) {
        Connection conn = DbUtil.getConn();
        PreparedStatement pstmt = null;

        var sql = String.join(" ", "insert into", dbTable, "(email, password, salt, uname, avatar, create_at)", "values(?, ?, ?, ?, ?, ?)");

        var isOk = false;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getSalt());
            pstmt.setString(4, user.getUname());
            pstmt.setString(5, user.getAvatar());
            pstmt.setLong(6, user.getCreateAt());

            System.out.println(pstmt.toString());

            isOk = pstmt.executeUpdate() > 0;
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
        return isOk;
    }

    public boolean update(UserModel user) {
        Connection conn = DbUtil.getConn();
        PreparedStatement pstmt = null;
        var isOk = false;
        var setVal = String.join(",", "password = ?", "uname = ?", "avatar = ?", "delete_timestamp = ?");
        var where = "where email = ?";
        var sql = String.join(" ", "update", dbTable, "set", setVal, where);

        try {
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, user.getPassword());
            pstmt.setString(2, user.getUname());
            pstmt.setString(3, user.getAvatar());
            pstmt.setLong(4, user.getDeleteTimestamp());
            pstmt.setString(5, user.getEmail());
            isOk = pstmt.executeUpdate() > 0;
        } catch (SQLException se) {
            System.out.println(se);
        }

        return isOk;
    }

    public List<UserModel> findAll(UserModel user) {
        Connection conn = DbUtil.getConn();
        PreparedStatement pstmt = null;

        var select = "select id, email, salt, uname, avatar, password, create_at, delete_timestamp from";
        // 拼接sql
        var sql = String.join(" ", select, dbTable, where(user));
        var output = new ArrayList<UserModel>();
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt = pstmtSetParam(pstmt, user);
            ResultSet result = pstmt.executeQuery();

            while (result.next()) {
                var item = new UserModel();
                item.setId(result.getInt("id"));
                item.setEmail(result.getString("email"));
                item.setSalt(result.getString("salt"));
                item.setUname(result.getString("uname"));
                item.setAvatar(result.getString("avatar"));
                item.setPassword(result.getString("password"));
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

    public List<UserModel> searchFriend(String info) {
        Connection conn = DbUtil.getConn();
        PreparedStatement pstmt = null;

        var select = "select id, email, salt, uname, avatar, password, create_at, delete_timestamp from";
        var where = "where email like ? or uname like ? and delete_timestamp = 0";
        // 拼接sql
        var sql = String.join(" ", select, dbTable, where);
        var output = new ArrayList<UserModel>();
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + info + "%");
            pstmt.setString(2, "%" + info + "%");

            ResultSet result = pstmt.executeQuery();

            while (result.next()) {
                var item = new UserModel();
                item.setId(result.getInt("id"));
                item.setEmail(result.getString("email"));
                item.setSalt(result.getString("salt"));
                item.setUname(result.getString("uname"));
                item.setAvatar(result.getString("avatar"));
                item.setPassword(result.getString("password"));
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

    public List<UserModel> findFriend(UserModel user) {
        var contactService = new ContactService();
        var contact = new ContactModel();
        contact.setOwnerEmail(user.getEmail());
        contact.setCate(ContactModel.CONCAT_CATE_USER);
        var contactList = contactService.findAll(contact);

        var output = findByContactList(contactList);

        return output;
    }

    public List<UserModel> findByContactList(List<ContactModel> conList) {
        var output = new ArrayList<UserModel>();
        for (ContactModel conItem : conList) {
            var user = new UserModel();
            switch (conItem.getCate()) {
                // 好友
                case ContactModel.CONCAT_CATE_USER: {
                    user.setEmail(conItem.getDstObj());
                    break;
                }
                // 群
                case ContactModel.CONCAT_CATE_COMUNITY: {
                    user.setEmail(conItem.getOwnerEmail());
                    break;
                }
                default: {
                    System.out.println("Error: 无法识别dstObj");
                }
            }
            user = find(user);
            output.add(user);
        }

        return output;
    }

    private String where(UserModel user, boolean ...normals) {
        var normal = normals.length > 0 ? normals[0] : true;

        StringJoiner where = new StringJoiner(" and ");

        if (user.getId() > 0) {
            where.add("id = ?");
        }
        if (user.getEmail() != null) {
            where.add("email = ?");
        }
        if (user.getPassword() != null) {
            where.add("password = ?");
        }
        if (user.getUname() != null) {
            where.add("uname = ?");
        }
        if (user.getCreateAt() != 0) {
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

    private PreparedStatement pstmtSetParam(PreparedStatement pstmt, UserModel user) throws SQLException {
        int paraIndex = 1;
        if (user.getId() > 0) {
            pstmt.setInt(paraIndex++, user.getId());
        }
        if (user.getEmail() != null) {
            pstmt.setString(paraIndex++, user.getEmail());
        }
        if (user.getPassword() != null) {
            pstmt.setString(paraIndex++, user.getPassword());
        }
        if (user.getUname() != null) {
            pstmt.setString(paraIndex++, user.getUname());
        }
        if (user.getCreateAt() != 0) {
            pstmt.setLong(paraIndex++, user.getCreateAt());
        }

        return pstmt;
    }
}
