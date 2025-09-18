package org.example.backend.entity.vo.response;

import lombok.Data;
import org.example.backend.entity.vo.BaseData;

import java.util.Date;

@Data
public class AuthorizeVO implements BaseData {
    String username;
    String role;
    String token;
    Date expiration;
}
