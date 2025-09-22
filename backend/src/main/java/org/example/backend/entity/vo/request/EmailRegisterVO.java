package org.example.backend.entity.vo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.example.backend.entity.vo.BaseData;
import org.hibernate.validator.constraints.Length;

@Data
public class EmailRegisterVO implements BaseData {
    @Email
    String email;
    @Length(min = 6, max = 6)
    String code;
    @Pattern(regexp = "^[a-zA-Z0-9]+$")
    @Length(max = 10, min = 1)
    String username;
    @Length(min = 6, max = 20)
    String password;

}
