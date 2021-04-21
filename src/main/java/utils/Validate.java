package utils;

import model.CommunityModel;
import model.ContactModel;
import model.UserModel;
import service.CommunityService;
import service.ContactService;
import service.UserService;

import java.util.regex.Pattern;

public class Validate {
    // 验证password是否合法（由数字和字母组成，并且要同时含有数字和字母，且长度要在6-16位之间）
    public static boolean validatePasswd(String passwd) {
        return Pattern.matches("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$",passwd);
    }

    // 检测Email是否合法
    public static boolean validateEmail(String email) {
        return Pattern.matches("\\w+@(\\w+.)+[a-z]{2,3}", email);
    }

    // 检测Password是否正确
    public static boolean validatePasswdEquals(String passwd, String plainpwd, String salt) {
        return passwd.equals(Rand.md5Encode(plainpwd + salt));
    }

    // 验证添加好友（群）参数合法性
    public static boolean validateAddContact(ContactModel contact) {
        if (contact.getOwnerEmail() == null || contact.getDstObj() == null || contact.getCate() == 0) {
            return false;
        }

        var userSer = new UserService();
        var user1 = new UserModel();
        // 验证当前用户是否存在
        user1.setEmail(contact.getOwnerEmail());
        user1 = userSer.find(user1);
        if (user1.getEmail() == null) {
            return false;
        }
        // 验证对方是否存在
        switch (contact.getCate()) {
            case ContactModel.CONCAT_CATE_USER: {
                if (contact.getOwnerEmail().equals(contact.getDstObj())) {
                    return false;
                }

                var user2 = new UserModel();
                user2.setEmail(contact.getDstObj());
                user2 = userSer.find(user2);
                if (user2.getEmail() == null) {
                    return false;
                }
                break;
            }
            case ContactModel.CONCAT_CATE_COMUNITY: {
                var comm = new CommunityModel();
                comm.setId(Integer.parseInt(contact.getDstObj()));
                comm = (new CommunityService()).find(comm);
                if (comm.getId() == 0) {
                    return false;
                }
                break;
            }
        }

        // 验证是否已经是好友
        var result = (new ContactService()).find(contact);
        if (result.getId() > 0) {
            return false;
        }

        return true;
    }

}
