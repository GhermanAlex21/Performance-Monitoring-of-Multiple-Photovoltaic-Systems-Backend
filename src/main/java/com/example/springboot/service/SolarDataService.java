package com.example.springboot.service;

import com.example.springboot.model.SolarApiResponse;
import com.example.springboot.model.SolarData;
import com.example.springboot.repository.SolarDataRepository;
import com.example.springboot.service.FirebaseService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SolarDataService {
    @Autowired
    private SolarDataRepository solarDataRepository;
    @Autowired
    private FirebaseService firebaseService;  // Inject your FirebaseService
    @Autowired
    private RestTemplate restTemplate;

    @PostConstruct
    @Scheduled(cron = "0 0 */6 * * *")  // Runs every 6 hours
    public void fetchAndSaveSolarData() {
        final String baseUrl = "https://api.solar.sheffield.ac.uk/pvlive/api/v4/pes/";
        int[] pesIds = {0, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23};  // All PES IDs

        LocalDateTime endDateTime = LocalDateTime.now();  // until now
        LocalDateTime startDateTime = endDateTime.minusDays(7);  // starting from 7 days ago

        for (int pesId : pesIds) {
            String url = String.format("%s%s?start=%s&end=%s", baseUrl, pesId,
                    startDateTime.format(DateTimeFormatter.ISO_DATE_TIME),
                    endDateTime.format(DateTimeFormatter.ISO_DATE_TIME));
            SolarApiResponse response = restTemplate.getForObject(url, SolarApiResponse.class);
            if (response != null && response.getData() != null) {
                saveData(response, pesId);
            }
        }
    }
    public List<SolarData> getSolarDataByPesId(Integer pesId) {
        return solarDataRepository.findByPesId(pesId);
    }

    @Scheduled(cron = "0 0 0 * * *")  // Runs at midnight every day
    @PostConstruct
    public void updateFirebaseWithAverages() {
        List<SolarData> allData = solarDataRepository.findAll();

        // Daily averages
        Map<Integer, Map<LocalDate, Double>> dailyAverages = calculateDailyAverages(allData);
        dailyAverages.forEach((pesId, averages) -> {
            averages.forEach((date, average) -> {
                firebaseService.saveDailyAverage(pesId, date, average);
            });
        });

        // Monthly averages
        Map<Integer, Map<YearMonth, Double>> monthlyAverages = calculateMonthlyAverages(allData);
        monthlyAverages.forEach((pesId, averages) -> {
            averages.forEach((month, average) -> {
                firebaseService.saveMonthlyAverage(pesId, month, average);
            });
        });

        // Weekly averages
        Map<Integer, Map<Integer, Double>> weeklyAverages = calculateWeeklyAverages(allData);
        weeklyAverages.forEach((pesId, averages) -> {
            averages.forEach((week, average) -> {
                LocalDate weekStartDate = calculateStartDateOfWeek(week);
                firebaseService.saveWeeklyAverage(pesId, week, average, weekStartDate);
            });
        });

        // Comparison data
        updateComparisonData();
    }

    private void updateComparisonData() {
        List<SolarData> allData = solarDataRepository.findAll();
        Map<Integer, Map<String, Object>> comparisonData = calculateComparisonData(allData);
        comparisonData.forEach((pesId, data) -> {
            firebaseService.saveComparisonData(pesId, data);
        });
    }

    private Map<Integer, Map<String, Object>> calculateComparisonData(List<SolarData> data) {
        return data.stream()
                .collect(Collectors.groupingBy(
                        SolarData::getPesId,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                this::calculateStats
                        )
                ));
    }

    private Map<String, Object> calculateStats(List<SolarData> solarDataList) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("averageEnergy", calculateAverageEnergy(solarDataList));
        stats.put("maxEnergy", calculateMaxEnergy(solarDataList));
        stats.put("totalEnergy", calculateTotalEnergy(solarDataList));
        stats.put("medianEnergy", calculateMedianEnergy(solarDataList));
        stats.put("totalHours", calculateTotalHours(solarDataList));
        stats.put("capacityFactor", calculateCapacityFactor(solarDataList));
        return stats;
    }

    private double calculateAverageEnergy(List<SolarData> solarDataList) {
        return solarDataList.stream().mapToDouble(SolarData::getGenerationMW).average().orElse(0);
    }

    private double calculateMaxEnergy(List<SolarData> solarDataList) {
        return solarDataList.stream().mapToDouble(SolarData::getGenerationMW).max().orElse(0);
    }

    private double calculateTotalEnergy(List<SolarData> solarDataList) {
        return solarDataList.stream().mapToDouble(SolarData::getGenerationMW).sum();
    }

    private double calculateMedianEnergy(List<SolarData> solarDataList) {
        List<Double> sorted = solarDataList.stream().map(SolarData::getGenerationMW).sorted().collect(Collectors.toList());
        int size = sorted.size();
        if (size == 0) return 0;
        return size % 2 == 0 ? (sorted.get(size / 2 - 1) + sorted.get(size / 2)) / 2.0 : sorted.get(size / 2);
    }

    private long calculateTotalHours(List<SolarData> solarDataList) {
        return solarDataList.size();
    }

    private double calculateCapacityFactor(List<SolarData> solarDataList) {
        double totalEnergy = calculateTotalEnergy(solarDataList);
        double maxCapacity = solarDataList.size() * solarDataList.stream().mapToDouble(SolarData::getGenerationMW).max().orElse(0);
        return totalEnergy / maxCapacity;
    }
    private LocalDate calculateStartDateOfWeek(int weekOfYear) {
        LocalDate date = LocalDate.now().with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, weekOfYear)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return date;
    }

    private Map<Integer, Map<Integer, Double>> calculateWeeklyAverages(List<SolarData> data) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        return data.stream()
                .filter(d -> d.getGenerationMW() > 0)
                .collect(Collectors.groupingBy(
                        SolarData::getPesId,
                        Collectors.groupingBy(
                                d -> d.getDatetimeGMT().get(weekFields.weekOfWeekBasedYear()),
                                Collectors.averagingDouble(SolarData::getGenerationMW)
                        )
                ));
    }

    private Map<Integer, Map<LocalDate, Double>> calculateDailyAverages(List<SolarData> data) {
        return data.stream()
                .filter(d -> d.getGenerationMW() > 0)
                .collect(Collectors.groupingBy(
                        SolarData::getPesId,
                        Collectors.groupingBy(
                                d -> d.getDatetimeGMT().toLocalDate(),
                                Collectors.averagingDouble(SolarData::getGenerationMW)
                        )
                ));
    }

    private Map<Integer, Map<YearMonth, Double>> calculateMonthlyAverages(List<SolarData> data) {
        return data.stream()
                .filter(d -> d.getGenerationMW() > 0)
                .collect(Collectors.groupingBy(
                        SolarData::getPesId,
                        Collectors.groupingBy(
                                d -> YearMonth.from(d.getDatetimeGMT()),
                                Collectors.averagingDouble(SolarData::getGenerationMW)
                        )
                ));
    }

    private void saveData(SolarApiResponse response, int pesId) {
        for (List<Object> record : response.getData()) {
            LocalDateTime dateTime = LocalDateTime.parse((String) record.get(1), DateTimeFormatter.ISO_DATE_TIME);
            Double generationMW = (Double) record.get(2);

            // Check if the record already exists to avoid duplicates
            if (!solarDataRepository.existsByDatetimeGMTAndPesId(dateTime, pesId)) {
                SolarData solarData = new SolarData(pesId, dateTime, generationMW);
                solarDataRepository.save(solarData);
                // Remove the line below to stop saving each record in Firebase "records" collection
                 firebaseService.saveSolarData(solarData);  // Save to Firebase
            }
        }
    }


}