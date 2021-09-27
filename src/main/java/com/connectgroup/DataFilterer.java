package com.connectgroup;

import com.connectgroup.model.LogDTO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataFilterer {
    private static final String SEPERATOR = ",";

    public static Collection<?> filterByCountry(Reader source, String country) {
        Predicate<LogDTO> countryFilter = item -> item.getCountryCode().equals(country);
        List<LogDTO> logList = readFile(source, countryFilter);

        return logList;
    }

    public static Collection<?> filterByCountryWithResponseTimeAboveLimit(Reader source, String country, long limit) {
        Predicate<LogDTO> countryAndLimitFilter = item -> item.getCountryCode().equals(country) && item.getResponseTime() > limit;
        List<LogDTO> logList = readFile(source, countryAndLimitFilter);

        return logList;
    }

    public static Collection<?> filterByResponseTimeAboveAverage(Reader source) {
        List<LogDTO> logList = readFile(source);
        double averageTime = getAverageResponseTime(logList);

        return logList.parallelStream().filter(t -> t.getResponseTime() > averageTime).collect(Collectors.toList());
    }

    public static List<String> getOrderLine(Reader source) throws IOException {
        String orderLine = ((BufferedReader) source).readLine();
        return Arrays.asList(orderLine.split(SEPERATOR));
    }

    public static List<LogDTO> readFile(Reader source) {
        try {
            List<String> orderLine = getOrderLine(source);
            List<LogDTO> logList = new ArrayList<>();
            String line;
            while ((line = ((BufferedReader) source).readLine()) != null) {
                LogDTO logDTO = mapDataToDTO(line, orderLine);
                if (!logDTO.isEmpty()) {
                    logList.add(logDTO);
                }
            }
            return logList;
        } catch (Exception e) {
            return List.of();
        }
    }

    public static List<LogDTO> readFile(Reader source, Predicate<LogDTO> filter) {
        try {
            List<String> orderLine = getOrderLine(source);
            List<LogDTO> logList = new ArrayList<>();
            String line;
            while ((line = ((BufferedReader) source).readLine()) != null) {
                LogDTO logDTO = mapDataToDTO(line, orderLine);
                if (!logDTO.isEmpty() && Stream.of(logDTO).anyMatch(filter)) {
                    logList.add(logDTO);
                }
            }
            return logList;
        } catch (Exception e) {
            return List.of();
        }
    }


    public static LogDTO mapDataToDTO(String data, List<String> orderLine) {
        LogDTO logDTO = new LogDTO();

        String[] seperatedLine = data.split(SEPERATOR);
        orderLine.stream().forEach(t -> {
            if (Order.REQUEST_TIMESTAMP.getValue().equals(t)) {
                logDTO.setRequestTimestamp(Long.valueOf(seperatedLine[orderLine.indexOf(t)]));
            } else if (Order.COUNTRY_CODE.getValue().equals(t)) {
                logDTO.setCountryCode(seperatedLine[orderLine.indexOf(t)]);
            } else if (Order.RESPONSE_TIME.getValue().equals(t)) {
                logDTO.setResponseTime(Integer.valueOf(seperatedLine[orderLine.indexOf(t)]));
            }
        });

        return logDTO;
    }

    public static double getAverageResponseTime(List<LogDTO> logList) {
        if (logList != null && logList.size() > 0)
            return logList.stream().mapToInt(LogDTO::getResponseTime).average().getAsDouble();
        return Double.MAX_VALUE;
    }

    enum Order {
        REQUEST_TIMESTAMP("REQUEST_TIMESTAMP"),
        COUNTRY_CODE("COUNTRY_CODE"),
        RESPONSE_TIME("RESPONSE_TIME");

        private String value;

        Order(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

}