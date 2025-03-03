package vn.edu.iuh.fit.constants;

public class FriendRequestConstants {

    // Các mã lỗi
    public static final int CODE_SUCCESS = 200;
    public static final int CODE_BAD_REQUEST = 400;
    public static final int CODE_NOT_FOUND = 404;

    // Các thông báo thành công
    public static final String FRIEND_REQUEST_SENT = "Gửi lời mời kết bạn thành công!";
    public static final String FRIEND_REQUEST_ACCEPTED = "Đã chấp nhận lời mời kết bạn!";
    public static final String FRIEND_REQUEST_DECLINED = "Lời mời kết bạn đã bị từ chối.";
    public static final String FRIEND_REQUEST_CANCELLED = "Lời mời kết bạn đã được hủy.";

    // Các thông báo lỗi
    public static final String FRIEND_REQUEST_FAILED = "Gửi lời mời kết bạn thất bại.";
    public static final String FRIEND_REQUEST_CANNOT_CANCEL = "Không thể hủy lời mời kết bạn vì nó người nhận đã chấp nhận.";
    public static final String FRIEND_REQUEST_CANNOT_ACCEPT = "Không thể chấp nhận lời mời kết bạn.";
    public static final String FRIEND_REQUEST_CANNOT_DECLINE = "Không thể từ chối lời mời kết bạn.";
    public static final String FRIEND_REQUEST_INVALID = "Không thể gửi lời mời cho chính mình.";
    public static final String FRIEND_REQUEST_ALREADY_FRIENDS = "Bạn đã là bạn bè, không thể gửi lại lời mời.";
    public static final String FRIEND_REQUEST_PENDING = "Đã có lời mời kết bạn đang chờ, không thể gửi lại.";
    public static final String FRIEND_REQUEST_REVERSED_PENDING = "Đã có lời mời kết bạn từ phía người nhận đang chờ.";
    public static final String FRIEND_REQUEST_NOT_FOUND = "Không tìm thấy lời mời kết bạn giữa hai người.";
}
