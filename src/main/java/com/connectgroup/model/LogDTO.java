package com.connectgroup.model;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Oğuzhan Karaduman
 * @created 25/09/2021 - 15:20
 */
public class LogDTO {
    private Long requestTimestamp;
    private String countryCode;
    private Integer responseTime;

    public LogDTO() {
    }

    public LogDTO(Long requestTimestamp, String countryCode, Integer responseTime) {
        this.requestTimestamp = requestTimestamp;
        this.countryCode = countryCode;
        this.responseTime = responseTime;
    }

    public Long getRequestTimestamp() {
        return requestTimestamp;
    }

    public void setRequestTimestamp(Long requestTimestamp) {
        this.requestTimestamp = requestTimestamp;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Integer getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Integer responseTime) {
        this.responseTime = responseTime;
    }

    public boolean isEmpty(){
        return Stream.of(this.requestTimestamp, this.countryCode, this.responseTime).allMatch(Objects::isNull);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogDTO logDTO = (LogDTO) o;
        return Objects.equals(requestTimestamp, logDTO.requestTimestamp) && Objects.equals(countryCode, logDTO.countryCode) && Objects.equals(responseTime, logDTO.responseTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestTimestamp, countryCode, responseTime);
    }
}
