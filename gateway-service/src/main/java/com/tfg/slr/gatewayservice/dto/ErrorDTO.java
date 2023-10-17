package com.tfg.slr.gatewayservice.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ErrorDTO {

    private String errorCode;
    private String errorMessage;
    private String details;
}
