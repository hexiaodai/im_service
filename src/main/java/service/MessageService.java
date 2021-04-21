package service;

import model.MessageModel;
import utils.Db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class MessageService {
    private Db DbUtil;
    private String dbTable = "message";

    public String getDbTable() {
        return dbTable;
    }

    public boolean insert(MessageModel msg) {
        Connection conn = DbUtil.getConn();
        PreparedStatement pstmt = null;

        var sql = String.join(" ", "insert into", dbTable, "(user_email, cmd, dst_obj, msg_type, content, create_at)", "values(?, ?, ?, ?, ?, ?)");

        var isOk = false;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, msg.getUserEmail());
            pstmt.setInt(2, msg.getCmd());
            pstmt.setString(3, msg.getDstObj());
            pstmt.setInt(4, msg.getMsgType());
            pstmt.setString(5, msg.getContent());
            pstmt.setLong(6, msg.getCreateAt());

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

    public List<MessageModel> findAll(MessageModel msg, String sort) {
        Connection conn = DbUtil.getConn();
        PreparedStatement pstmt = null;

        var select = "select id, user_email, cmd, dst_obj, msg_type, content, create_at, delete_timestamp from";
        // 拼接sql
        var sql = String.join(" ", select, dbTable, where(msg), "order by", sort);
        var output = new ArrayList<MessageModel>();
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt = pstmtSetParam(pstmt, msg);
            ResultSet result = pstmt.executeQuery();

            while (result.next()) {
                var item = new MessageModel();
                item.setId(result.getInt("id"));
                item.setUserEmail(result.getString("user_email"));
                item.setCmd(result.getInt("cmd"));
                item.setDstObj(result.getString("dst_obj"));
                item.setMsgType(result.getInt("msg_type"));
                item.setContent(result.getString("content"));
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

    public List<MessageModel> findFriend(String userEmail, String dstObj) {
        Connection conn = DbUtil.getConn();
        PreparedStatement pstmt = null;

        var select = "select id, user_email, cmd, dst_obj, msg_type, content, create_at, delete_timestamp from";
        var where = String.join(" ", "where", "(user_email = ? and dst_obj = ?)", "or (user_email = ? and dst_obj = ?)", "and cmd = ?");
        // 拼接sql
        var sql = String.join(" ", select, dbTable, where, "order by create_at asc");
        var output = new ArrayList<MessageModel>();
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userEmail);
            pstmt.setString(2, dstObj);
            pstmt.setString(3, dstObj);
            pstmt.setString(4, userEmail);
            pstmt.setInt(5, MessageModel.CMD_SINGLE_MSG);

            ResultSet result = pstmt.executeQuery();

            while (result.next()) {
                var item = new MessageModel();
                item.setId(result.getInt("id"));
                item.setUserEmail(result.getString("user_email"));
                item.setCmd(result.getInt("cmd"));
                item.setDstObj(result.getString("dst_obj"));
                item.setMsgType(result.getInt("msg_type"));
                item.setContent(result.getString("content"));
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

    private String where(MessageModel msg, boolean ...normals) {
        var normal = normals.length > 0 ? normals[0] : true;

        StringJoiner where = new StringJoiner(" and ");

        if (msg.getId() > 0) {
            where.add("id = ?");
        }
        if (msg.getUserEmail() != null) {
            where.add("user_email = ?");
        }
        if (msg.getCmd() != 0) {
            where.add("cmd = ?");
        }
        if (msg.getDstObj() != null) {
            where.add("dst_obj = ?");
        }
        if (msg.getMsgType() != 0) {
            where.add("msg_type = ?");
        }
        if (msg.getContent() != null) {
            where.add("content = ?");
        }
        if (msg.getCreateAt() != 0) {
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

    private PreparedStatement pstmtSetParam(PreparedStatement pstmt, MessageModel msg) throws SQLException {
        int paraIndex = 1;
        if (msg.getId() > 0) {
            pstmt.setInt(paraIndex++, msg.getId());
        }
        if (msg.getUserEmail() != null) {
            pstmt.setString(paraIndex++, msg.getUserEmail());
        }
        if (msg.getCmd() != 0) {
            pstmt.setInt(paraIndex++, msg.getCmd());
        }
        if (msg.getDstObj() != null) {
            pstmt.setString(paraIndex++, msg.getDstObj());
        }
        if (msg.getMsgType() != 0) {
            pstmt.setInt(paraIndex++, msg.getMsgType());
        }
        if (msg.getContent() != null) {
            pstmt.setString(paraIndex++, msg.getContent());
        }
        if (msg.getCreateAt() != 0) {
            pstmt.setLong(paraIndex++, msg.getCreateAt());
        }

        return pstmt;
    }
}
