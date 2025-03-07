package vn.edu.iuh.fit.constants;

public class AuthConstants {

    // Thông báo đăng nhập
    public static final String MESSAGE_LOGIN_SUCCESS = "Đăng nhập thành công";
    public static final String MESSAGE_LOGIN_FAILED = "Số điện thoại hoặc mật khẩu không chính xác";

    // Thông báo Refresh Token
    public static final String MESSAGE_REFRESH_TOKEN_SUCCESS = "Cấp lại Access Token thành công";
    public static final String MESSAGE_REFRESH_TOKEN_INVALID = "Refresh Token không hợp lệ";
    public static final String MESSAGE_REFRESH_TOKEN_EXPIRED = "Refresh Token đã hết hạn";
    public static final String MESSAGE_REFRESH_TOKEN_REQUIRED = "Refresh Token không được để trống";

    // Thông báo đăng xuất
    public static final String MESSAGE_LOGOUT_SUCCESS = "Đăng xuất thành công";

    // Thông báo người dùng
    public static final String MESSAGE_USER_NOT_FOUND = "Người dùng không tồn tại";
    public static final String MESSAGE_USER_ID_NOT_FOUND = "Không tìm thấy người dùng với ID đã cung cấp";

    public static final String MESSAGE_ACCESS_TOKEN_REQUIRED = "Vui lòng cung cấp Access Token để đăng xuất";
    public static final String MESSAGE_INVALID_ACCESS_TOKEN = "Access Token không hợp lệ";

    public static final String MESSAGE_TOKEN_HAS_BEEN_DISABLED = "Access Token đã bị vô hiệu hóa";
    public static final String MESSAGE_TOKEN_EXPIRED = "Access Token đã hết hạn!";

    // Thêm thông báo lỗi token
    public static final String MESSAGE_INVALID_SIGNATURE = "Chữ ký token không hợp lệ";
    public static final String MESSAGE_INVALID_JWT_FORMAT = "Định dạng JWT không hợp lệ";
    public static final String MESSAGE_MISSING_OR_INVALID_BEARER = "Authorization header bị thiếu hoặc không đúng định dạng Bearer";
    public static final String MESSAGE_MALFORMED_JWT = "Token JWT không đúng định dạng";
    public static final String MESSAGE_EXPIRED_JWT = "Token JWT đã hết hạn";
    public static final String MESSAGE_INVALID_JWT_SIGNATURE = "Chữ ký JWT không hợp lệ";
    public static final String MESSAGE_JWT_AUTHENTICATION_ERROR = "Lỗi xác thực JWT";
}
