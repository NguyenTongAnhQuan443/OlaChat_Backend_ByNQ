package vn.edu.iuh.fit.dtos;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendRequestDTO {
    private UUID id;
    private UserDTO sender;
    private UserDTO receiver;
}
