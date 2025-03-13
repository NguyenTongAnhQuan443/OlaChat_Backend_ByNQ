package vn.edu.iuh.fit.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import vn.edu.iuh.fit.dtos.RegisterUserDTO;
import vn.edu.iuh.fit.dtos.UserDTO;
import vn.edu.iuh.fit.models.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO toUserDTO(User user);

    @Mapping(target = "id", ignore = true) // Không set ID vì nó tự động sinh
    @Mapping(target = "password", ignore = true) // Password sẽ được xử lý riêng
    @Mapping(target = "authProvider", expression = "java(vn.edu.iuh.fit.enums.AuthProvider.LOCAL)") // Mặc định là LOCAL
    @Mapping(target = "role", expression = "java(vn.edu.iuh.fit.enums.Role.USER)") // Mặc định là USER
    @Mapping(target = "status", expression = "java(vn.edu.iuh.fit.enums.UserStatus.ACTIVE)") // Mặc định ACTIVE
    User toUser(RegisterUserDTO registerUserDTO);
}
