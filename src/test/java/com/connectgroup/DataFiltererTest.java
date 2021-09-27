package com.connectgroup;

import com.connectgroup.model.LogDTO;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class DataFiltererTest {
    BufferedReader reader;

    @Before
    public void setup() throws FileNotFoundException {
        reader = new BufferedReader(new FileReader("src/test/resources/multi-lines"));
    }

    @After
    public void tearDown() throws IOException {
        reader.close();
    }

    @Test
    public void shouldReturnEmptyCollection_WhenLogFileIsEmpty() throws FileNotFoundException {
        assertTrue(DataFilterer.filterByCountry(openFile("src/test/resources/empty"), "GB").isEmpty());
    }

    @Test
    public void shouldReturnCollection_WhenFilterByCountry() {
        //GIVEN
        List<LogDTO> expected = new ArrayList<>();
        expected.add(new LogDTO(1432917066L, "GB", 37));

        //WHEN
        List<LogDTO> actual = (List<LogDTO>) DataFilterer.filterByCountry(reader, "GB");

        //THEN
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnCollection_WhenFilterByCountryAndResponseTimeLimit() {
        //GIVEN
        List<LogDTO> expected = new ArrayList<>();
        expected.add(new LogDTO(1433666287L, "US", 789));
        expected.add(new LogDTO(1432484176L, "US", 850));

        String country = "US";
        long limit = 600;

        //WHEN
        List<LogDTO> actual = (List<LogDTO>) DataFilterer.filterByCountryWithResponseTimeAboveLimit(reader, country, limit);

        //THEN
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnCollection_WhenFilterByResponseTimeAboveAverage() {
        //GIVEN
        List<LogDTO> expected = new ArrayList<>();
        expected.add(new LogDTO(1433190845L, "US", 539));
        expected.add(new LogDTO(1433666287L, "US", 789));
        expected.add(new LogDTO(1432484176L, "US", 850));


        //WHEN
        List<LogDTO> actual = (List<LogDTO>) DataFilterer.filterByResponseTimeAboveAverage(reader);

        //THEN
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnOrderLine_WhenReadFirstLine() throws IOException {
        List<String> order = DataFilterer.getOrderLine(reader);
        Assert.assertNotNull(order);
    }

    @Test
    public void shouldMapLineToDTO_WhenReadLine() throws IOException {
        //GIVEN
        List<String> orderLine = DataFilterer.getOrderLine(reader);
        String dataLine = reader.readLine();

        //WHEN
        LogDTO logDTO = DataFilterer.mapDataToDTO(dataLine, orderLine);

        //THEN
        Assert.assertNotNull(logDTO);
        Assert.assertFalse(logDTO.isEmpty());
    }

    @Test
    public void shouldReadAllLines_WhenReadFile() {
        //GIVEN

        //WHEN
        List<LogDTO> logList = DataFilterer.readFile(reader);

        //THEN
        Assert.assertNotNull(logList);
        Assert.assertTrue(logList.size()>0);
    }

    /**
     * Delta, The maximum delta between expected and actual for which both numbers are still considered equal.
     * assertEquals(double, double) is deprecated because the 2 doubles may be the same but if they are calculated
     * values, the processor may make them slightly different values. So, that's why delta is used.
     */
    @Test
    public void shouldCalculateAverageTime_WhenGetAverangeResponseTime() {
        //GIVEN
        double expected = 526.0;
        double delta = 0;

        //WHEN
        List<LogDTO> logList = DataFilterer.readFile(reader);
        double actual = DataFilterer.getAverageResponseTime(logList);

        //THEN
        Assert.assertEquals(expected, actual,delta);
    }

    @Test
    public void shouldReturnMaxValue_WhenLogListIsEmptyWhileCalculatingAverageTime() throws IOException {
        //GIVEN
        double expected = Double.MAX_VALUE;
        double delta = 0;

        //WHEN
        List<LogDTO> logList = new ArrayList<>();
        double actual = DataFilterer.getAverageResponseTime(logList);

        //THEN
        Assert.assertEquals(expected, actual,delta);
    }

    private BufferedReader openFile(String filename) throws FileNotFoundException {
        return new BufferedReader(new FileReader(filename));
    }
}
