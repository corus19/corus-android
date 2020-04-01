package com.jamdoli.corus.api.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoResponse {

    private String id;
    private String bluetoothSignature;
    private String covidContactStatus;
    private String sendContactDetails;
}
