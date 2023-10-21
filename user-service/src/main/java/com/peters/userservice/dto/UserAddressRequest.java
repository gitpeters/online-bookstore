package com.peters.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserAddressRequest {
    private String phoneNumber;
    private String street;
    private String city;
    private String state;
    private String country;
}
