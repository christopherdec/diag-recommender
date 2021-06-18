package com.example.computerconfiguration.base;

import com.example.computerconfiguration.domain.CompleteConfiguration;
import com.example.computerconfiguration.domain.ConfigurationModel;
import com.example.computerconfiguration.domain.computer.Computer;
import com.example.computerconfiguration.domain.computer.ComputerModel;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.chocosolver.util.tools.ArrayUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Loads computer configuration base from ComputerBase class
 * Provides artificially generated configurations if the base is empty,
 * read from a .csv file.
 */
public class ComputerLoader implements ConfigurationLoader {

    private static ComputerLoader instance;

    private static final String[] ZERO = {"nao"};
    private static final String[] ONE = {"basico", "mini", "dual-core", "sim", "4GB"};
    private static final String[] TWO = {"desenvolvimento", "desktop", "quad-core", "8GB"};
    private static final String[] THREE = {"games", "gamer", "16GB"};

    private ComputerLoader() {
    }

    public static ComputerLoader getInstance() {
        if (instance == null) {
            instance = new ComputerLoader();
        }
        return instance;
    }

    public List<? extends CompleteConfiguration> getConfigurations() {

        return ComputerBase.base;
    }

    public List<Computer> getArtificialConfigurations() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classloader.getResourceAsStream("complete-configurations-base.csv");

        CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(inputStream))
                .withSkipLines(1)
                .build();

        List<Computer> computers = new ArrayList<>();

        for(String[] row : csvReader) {
            computers.add(new Computer(Arrays.stream(row).map(ComputerLoader::cellParserInt).collect(Collectors.toList())));
        }
        return computers;
    }

    public ConfigurationModel getDomainClass() {
        return new ComputerModel();
    }

    private static int cellParserInt(String cell) {

        if (ArrayUtils.contains(ZERO, cell)) { return 0; }
        if (ArrayUtils.contains(ONE, cell)) { return 1; }
        if (ArrayUtils.contains(TWO, cell)) { return 2; }
        if (ArrayUtils.contains(THREE, cell)) { return 3; }
        return Integer.parseInt(cell);
    }
}
