package vn.edu.iuh.fit.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class FriendRequestActionDTO {
    @NotNull(message = "Sender ID không được để trống")
    private UUID sender;

    @NotNull(message = "Receiver ID không được để trống")
    private UUID receiver;
}
