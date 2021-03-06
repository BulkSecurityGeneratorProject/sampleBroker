package com.arris.cloudng.wifibroker.service.dto;

import java.io.Serializable;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;






/**
 * Criteria class for the Zone entity. This class is used in ZoneResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /zones?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ZoneCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private StringFilter serviceId;

    private StringFilter deviceId;

    private StringFilter serviceName;

    private StringFilter deviceName;

    private LongFilter apId;

    private LongFilter wlanGroupId;

    private LongFilter wlanId;

    private LongFilter domainId;

    public ZoneCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getServiceId() {
        return serviceId;
    }

    public void setServiceId(StringFilter serviceId) {
        this.serviceId = serviceId;
    }

    public StringFilter getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(StringFilter deviceId) {
        this.deviceId = deviceId;
    }

    public StringFilter getServiceName() {
        return serviceName;
    }

    public void setServiceName(StringFilter serviceName) {
        this.serviceName = serviceName;
    }

    public StringFilter getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(StringFilter deviceName) {
        this.deviceName = deviceName;
    }

    public LongFilter getApId() {
        return apId;
    }

    public void setApId(LongFilter apId) {
        this.apId = apId;
    }

    public LongFilter getWlanGroupId() {
        return wlanGroupId;
    }

    public void setWlanGroupId(LongFilter wlanGroupId) {
        this.wlanGroupId = wlanGroupId;
    }

    public LongFilter getWlanId() {
        return wlanId;
    }

    public void setWlanId(LongFilter wlanId) {
        this.wlanId = wlanId;
    }

    public LongFilter getDomainId() {
        return domainId;
    }

    public void setDomainId(LongFilter domainId) {
        this.domainId = domainId;
    }

    @Override
    public String toString() {
        return "ZoneCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (serviceId != null ? "serviceId=" + serviceId + ", " : "") +
                (deviceId != null ? "deviceId=" + deviceId + ", " : "") +
                (serviceName != null ? "serviceName=" + serviceName + ", " : "") +
                (deviceName != null ? "deviceName=" + deviceName + ", " : "") +
                (apId != null ? "apId=" + apId + ", " : "") +
                (wlanGroupId != null ? "wlanGroupId=" + wlanGroupId + ", " : "") +
                (wlanId != null ? "wlanId=" + wlanId + ", " : "") +
                (domainId != null ? "domainId=" + domainId + ", " : "") +
            "}";
    }

}
