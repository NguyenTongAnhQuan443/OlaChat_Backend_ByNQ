# Refresh Token
1. Lưu refresh token theo từng thiết bị (hoặc session)
   -> Mỗi thiết bị có một refresh token riêng.
2. Tạo refresh token mới khi người dùng đăng nhập từ thiết bị mới
   -> Nếu user đăng nhập từ thiết bị mới, tạo refresh token mới mà không ảnh hưởng đến các thiết bị cũ.
3. Xóa refresh token khi logout từng thiết bị
   -> Khi logout trên một thiết bị cụ thể, chỉ xóa refresh token của thiết bị đó.
4. AccessToken RS256 - RefreshToken : HMAC SHA256


# Quên mật khẩu
1. Người dùng nhập email vào form "Quên mật khẩu".
2. Hệ thống tạo OTP 6 số. - Lưu OTP vào Redis (hết hạn sau 5 phút).
3. Gửi OTP đến email của người dùng. - Người dùng nhập OTP vào App/Web.
4. Người dùng nhập OTP và mật khẩu mới.	- Nếu OTP đúng, cho phép đặt lại mật khẩu.
5. Xóa OTP khỏi Redis sau khi dùng.	- Đảm bảo OTP chỉ dùng 1 lần.
6.  Giới hạn yêu cầu OTP trong Redis với thời gian hết hạn 1 giờ