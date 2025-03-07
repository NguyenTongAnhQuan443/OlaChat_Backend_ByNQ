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

    public static final String MESSAGE_ACCESS_TOKEN_REQUIRED = "Vui lòng cung cấp Access Token để đăng xuất";
    public static final String MESSAGE_INVALID_ACCESS_TOKEN = "Access Token không hợp lệ";

    public static final String MESSAGE_TOKEN_HAS_BEEN_DISABLED = "Access Token đã bị vô hiệu hóa";
    public static final String MESSAGE_TOKEN_IS_INVALID = "Access Token đã hết hạn!";

    // Thêm thông báo lỗi token
    public static final String MESSAGE_INVALID_SIGNATURE = "Chữ ký token không hợp lệ";
}