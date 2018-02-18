package controller.web;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Connect {

    private String database;

    private String user;

    private String password;
}
