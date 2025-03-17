package vn.edu.iuh.fit.constants;

public class AuthConstants {

    // Thông báo đăng nhập
    public static final String MESSAGE_LOGIN_FAILED = "Số điện thoại hoặc mật khẩu không chính xác";

    // Thông báo đăng xuất
    public static final String MESSAGE_LOGOUT_SUCCESS = "Đăng xuất thành công";
    public static final String MESSAGE_DEVICE_LOGOUT_NOT_FOUND = "Thiết bị đăng xuất không tồn tại !";

    // Thông báo người dùng
    public static final String MESSAGE_USER_ID_NOT_FOUND = "Không tìm thấy người dùng với ID đã cung cấp";
    public static final String MESSAGE_INVALID_ACCESS_TOKEN = "Access Token không hợp lệ";
    public static final String MESSAGE_TOKEN_HAS_BEEN_DISABLED = "Access Token đã bị vô hiệu hóa";
    public static final String MESSAGE_TOKEN_EXPIRED = "Access Token đã hết hạn!";

    // Thêm thông báo lỗi token
    public static final String MESSAGE_INVALID_SIGNATURE = "Chữ ký token không hợp lệ";
    public static final String MESSAGE_INVALID_JWT_FORMAT = "Định dạng JWT không hợp lệ";
    public static final String MESSAGE_MALFORMED_JWT = "Token JWT không đúng định dạng";
    public static final String MESSAGE_JWT_AUTHENTICATION_ERROR = "Lỗi xác thực JWT";
}
