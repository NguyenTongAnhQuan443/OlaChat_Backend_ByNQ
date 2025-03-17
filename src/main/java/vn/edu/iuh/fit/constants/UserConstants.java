package vn.edu.iuh.fit.constants;

public class UserConstants {

    // OTP Messages
    public static final String MESSAGE_OTP_SENT = "Mã OTP đã được gửi đến số điện thoại của bạn.";
    public static final String MESSAGE_OTP_VERIFIED = "Xác thực OTP thành công, tài khoản đã được tạo!";
    public static final String MESSAGE_OTP_INVALID = "Mã OTP không hợp lệ hoặc đã hết hạn!";

    // User Messages
    public static final String MESSAGE_USER_NOT_FOUND = "Người dùng không tồn tại!";
    public static final String MESSAGE_PHONE_ALREADY_EXISTS = "Số điện thoại đã được sử dụng!";
    public static final String MESSAGE_USER_INFO_RETRIEVED = "Lấy thông tin thành công!";
    public static final String MESSAGE_REGISTRATION_SUCCESS = "Đăng ký thành công!";

    // Password Reset Messages
    public static final String MESSAGE_PASSWORD_RESET_OTP_SENT = "Mã OTP đã được gửi đến email của bạn.";
    public static final String MESSAGE_PASSWORD_UPDATED = "Mật khẩu đã được cập nhật.";
    public static final String MESSAGE_PASSWORD_RESET_LIMIT = "Bạn đã yêu cầu đặt lại mật khẩu gần đây. Vui lòng thử lại sau %d phút.";

    private UserConstants() {
    }
}