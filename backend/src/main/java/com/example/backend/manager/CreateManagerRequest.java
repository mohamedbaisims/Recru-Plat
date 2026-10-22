package com.example.backend.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateManagerRequest {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
}
