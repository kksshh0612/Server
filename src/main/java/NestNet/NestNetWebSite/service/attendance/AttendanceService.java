package NestNet.NestNetWebSite.service.attendance;

import NestNet.NestNetWebSite.api.ApiResult;
import NestNet.NestNetWebSite.domain.attendance.Attendance;
import NestNet.NestNetWebSite.domain.member.Member;
import NestNet.NestNetWebSite.dto.response.attendance.AttendanceStatisticsResponse;
import NestNet.NestNetWebSite.dto.response.attendance.MonthlyAttendanceStatisticsDto;
import NestNet.NestNetWebSite.dto.response.attendance.WeeklyAttendanceStatisticsDto;
import NestNet.NestNetWebSite.exception.CustomException;
import NestNet.NestNetWebSite.exception.ErrorCode;
import NestNet.NestNetWebSite.repository.attendance.AttendanceRepository;
import NestNet.NestNetWebSite.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final MemberRepository memberRepository;

    /*
    출석 -> 0시~24시까지 한번 가능
     */
    @Transactional
    public void saveAttendance(String memberLonginId, LocalDateTime currTime){

        Member member = memberRepository.findByLoginId(memberLonginId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_LOGIN_ID_NOT_FOUND));

        LocalDateTime startOfDay = currTime.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = currTime.toLocalDate().atTime(LocalTime.MAX);

        Optional<Attendance> prevAttendance = attendanceRepository.findByMemberAndDay(member, startOfDay, endOfDay);

        if(prevAttendance.isPresent()){
            throw new CustomException(ErrorCode.ALREADY_ATTENDED);
        }

        Attendance attendance = new Attendance(member, currTime);
        attendanceRepository.save(attendance);
    }

    /*
    주간 출석 조회
     */
    public List<WeeklyAttendanceStatisticsDto> findWeeklyAttendanceStatistics(LocalDateTime currTime){

        DayOfWeek currDayOfWeek = currTime.getDayOfWeek();     // 현재 요일 (1:월 7:일)
        LocalDate currDate = currTime.toLocalDate();
        LocalDateTime startDateTimeOfWeek = currDate.minusDays(currDayOfWeek.getValue() - 1).atStartOfDay();       // 이번주 시작일(Mon)의 시작 시간
        LocalDateTime endDateTimeOfWeek = currDate.plusDays(6).atTime(23, 59, 59);                        // 이번주 종료일(Sun)의 끝나는 시간

        List<Object[]> weeklyStatistics = attendanceRepository.findWeeklyStatisticsByMember(startDateTimeOfWeek, endDateTimeOfWeek);

        List<Object[]> top5RankWeeklyStatistics = weeklyStatistics.stream()
                .sorted((o1, o2) -> Long.compare(
                        Long.parseLong(String.valueOf(o2[1])),
                        Long.parseLong(String.valueOf(o1[1]))
                ))
                .limit(5)
                .collect(Collectors.toList());

        List<WeeklyAttendanceStatisticsDto> weeklyStatisticsDtoList = top5RankWeeklyStatistics.stream()
                .map(WeeklyAttendanceStatisticsDto::of)
                .toList();

        return  weeklyStatisticsDtoList;
    }

    /*
    월간 출석 조회
     */
    public List<MonthlyAttendanceStatisticsDto> findMonthlyAttendanceStatistics(LocalDateTime currTime){

        LocalDateTime startDateTimeOfMonth = currTime.toLocalDate().withDayOfMonth(1).atStartOfDay();       // 이번달 시작일의 시작 시간
        LocalDateTime endDateTimeOfMonth = currTime.toLocalDate().withDayOfMonth(currTime.toLocalDate().lengthOfMonth())
                .atTime(23, 59, 59);  // 이번달 종료일의 끝나는 시간

        List<Object[]> monthlyStatistics = attendanceRepository.findMonthlyStatisticsByMember(startDateTimeOfMonth, endDateTimeOfMonth);

        List<Object[]> top5RankMonthlyStatistics = monthlyStatistics.stream()
                .sorted((o1, o2) -> Long.compare(
                        Long.parseLong(String.valueOf(o2[1])),
                        Long.parseLong(String.valueOf(o1[1]))
                ))
                .limit(5)
                .collect(Collectors.toList());

        List<MonthlyAttendanceStatisticsDto> monthlyStatisticsDtoList = top5RankMonthlyStatistics.stream()
                .map(MonthlyAttendanceStatisticsDto::of)
                .toList();

        return monthlyStatisticsDtoList;
    }
}
