package NestNet.NestNetWebSite.dto.response.attendance;

import NestNet.NestNetWebSite.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyAttendanceStatisticsDto {

    private String memberName;
    private String type;
    private Long point;

    public static WeeklyAttendanceStatisticsDto of(Object[] statistics){
        return WeeklyAttendanceStatisticsDto.builder()
                .memberName(((Member) statistics[0]).getName())
                .type("weekly")
                .point(Long.parseLong(String.valueOf(statistics[1])) * 10)
                .build();
    }
}
