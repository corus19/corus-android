package com.jamdoli.corus.api.model;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class BulkDeviceRequest {

    private List<DeviceRequest> deviceRequests;
    private int sequenceNumber;

}
