package com.jamdoli.corus.api.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UserInfoRequest {

    private String bluetoothSignature;
    private String osVersion;
    private String os;
    private String deviceId;
    private String fcmId;

}
