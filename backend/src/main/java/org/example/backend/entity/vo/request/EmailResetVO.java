package org.example.backend.entity.vo.request;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.example.backend.entity.vo.BaseData;
import org.hibernate.validator.constraints.Length;

@Data
public class EmailResetVO implements BaseData {
    @Email
    String email;
    @Length(min = 6, max = 6)
    String code;
    @Length(min = 6, max = 20)
    String newPassword;
}
