package org.example.backend.entity.vo.response;

import lombok.Data;
import org.example.backend.entity.vo.BaseData;

@Data
public class AccountResponseVO implements BaseData {
    Integer accountId;
    String username;
    String email;
    String role;
    boolean enable;
}
