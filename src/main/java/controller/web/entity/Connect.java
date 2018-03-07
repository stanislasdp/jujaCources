package controller.web.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class Connect {

    @NotNull(message = "is required")
    @Size(min = 1, message = "size should be minimum 1")
    private String database;

    @NotNull(message = "is required")
    @Size(min = 1, message = "size should be minimum 1")
    private String user;

    private String password;
}
