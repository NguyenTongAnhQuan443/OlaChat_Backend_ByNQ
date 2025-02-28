package vn.edu.iuh.fit.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import vn.edu.iuh.fit.dtos.FriendRequestDTO;
import vn.edu.iuh.fit.models.FriendRequest;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface FriendRequestMapper {
    FriendRequestMapper INSTANCE = Mappers.getMapper(FriendRequestMapper.class);

    @Mapping(source = "sender", target = "sender")
    @Mapping(source = "receiver", target = "receiver")
    FriendRequestDTO toFriendRequestDTO(FriendRequest friendRequest);
}
