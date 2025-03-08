# Refresh Token
1. Lưu refresh token theo từng thiết bị (hoặc session)
-> Mỗi thiết bị có một refresh token riêng.
2. Tạo refresh token mới khi người dùng đăng nhập từ thiết bị mới
-> Nếu user đăng nhập từ thiết bị mới, tạo refresh token mới mà không ảnh hưởng đến các thiết bị cũ.
3. Xóa refresh token khi logout từng thiết bị
-> Khi logout trên một thiết bị cụ thể, chỉ xóa refresh token của thiết bị đó.