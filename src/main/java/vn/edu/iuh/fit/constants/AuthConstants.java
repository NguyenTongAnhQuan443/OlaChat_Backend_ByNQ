package vn.edu.iuh.fit.constants;

public class AuthConstants {

    // Thông báo đăng nhập
    public static final String MESSAGE_LOGIN_FAILED = "Số điện thoại hoặc mật khẩu không chính xác";
    public static final String MESSAGE_LOGIN_SUCCESS = "Đăng nhập thành công!";

    // Thông báo đăng xuất
    public static final String MESSAGE_LOGOUT_SUCCESS = "Đăng xuất thành công";


    // Thông báo người dùng
    public static final String MESSAGE_USER_ID_NOT_FOUND = "Không tìm thấy người dùng với ID đã cung cấp";

    // Access Token
    public static final String MESSAGE_INVALID_ACCESS_TOKEN = "Access Token không hợp lệ";
    public static final String MESSAGE_TOKEN_EXPIRED = "Access Token đã hết hạn!";

    // Refresh Token
    public static final String MESSAGE_INVALID_REFRESH_TOKEN = "Refresh Token không hợp lệ hoặc đã bị thay thế!";
    public static final String MESSAGE_REFRESH_TOKEN_EXPIRED = "Refresh Token đã hết hạn!";
    public static final String MESSAGE_REFRESH_TOKEN_NOT_FOUND = "Refresh Token không tồn tại!";

    // OAuth & JWT
    public static final String MESSAGE_INVALID_SIGNATURE = "Chữ ký token không hợp lệ";
    public static final String MESSAGE_INVALID_JWT_FORMAT = "Định dạng JWT không hợp lệ";
    public static final String MESSAGE_MALFORMED_JWT = "Token JWT không đúng định dạng";
    public static final String MESSAGE_JWT_AUTHENTICATION_ERROR = "Lỗi xác thực JWT";
    public static final String MESSAGE_OAUTH_PROVIDER_INVALID = "Phương thức OAuth không hợp lệ!";
    public static final String MESSAGE_OAUTH_TOKEN_INVALID = "Token OAuth không hợp lệ!";

    // Refresh thành công
    public static final String MESSAGE_REFRESH_SUCCESS = "Làm mới access token thành công!";
}
