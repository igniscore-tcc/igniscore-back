package com.igniscore.api.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordDTO {

    private String newPassword;
    private String confirmPassword;
}