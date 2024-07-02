package com.georgebanin.serviceimpl;

import com.georgebanin.dto.DashboardCardStatsDto;
import com.georgebanin.dto.ResponseDto;
import com.georgebanin.dto.StatsDto;
import com.georgebanin.repoository.IPModelRepository;
import com.georgebanin.repoository.PingResultRepository;
import com.georgebanin.service.StatisticsService;
import io.smallrye.mutiny.Uni;
import io.vertx.core.Vertx;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.time.Period;

@ApplicationScoped
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {

    @Inject
    IPModelRepository ipModelRepository;

    @Inject
    PingResultRepository pingResultRepository;

    @Inject
    Vertx vertx;


    @Override
    public Uni<ResponseDto> dashboardCardStats() {

        return ipModelRepository.countIpStats()
                .map(dashboardCardStatsDto ->
                        ResponseDto.transformToResponse("Successfull",200,dashboardCardStatsDto));
    }

    @Override
    public Uni<ResponseDto> calculateAvgLatencyByRange(StatsDto statsDto) {
        //check date range
        var fromDate = statsDto.fromDate();
        var toDate = statsDto.toDate();

        if(isDateDiffOneDay(fromDate, toDate)){
        return pingResultRepository.getAverageLatencyPerDay(fromDate,toDate)
                .collect().asList()
                .map(stats ->  ResponseDto.transformToResponse("Successfull",200,stats));
        }
        if(isDateDiffOneWeek(fromDate,toDate)){
            return pingResultRepository.getAverageLatencyPerWeek(fromDate,toDate)
                    .collect().asList()
                    .map(stats ->  ResponseDto.transformToResponse("Successfull",200,stats));
        }
        return null;
    }

    private boolean isDateDiffOneDay(OffsetDateTime fromDate, OffsetDateTime toDate) {
        // Calculate the difference in days
        Period period = Period.between(fromDate.toLocalDate(), toDate.toLocalDate());
        return period.getDays() <= 1;
    }

    private boolean isDateDiffOneWeek(OffsetDateTime fromDate, OffsetDateTime toDate) {
        // Calculate the difference in days
        Period period = Period.between(fromDate.toLocalDate(), toDate.toLocalDate());
        return period.getDays() == 7;
    }
}
