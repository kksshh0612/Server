package NestNet.NestNetWebSite.service.attendance;

import NestNet.NestNetWebSite.TestSupport;
import NestNet.NestNetWebSite.domain.attendance.Attendance;
import NestNet.NestNetWebSite.domain.member.Member;
import NestNet.NestNetWebSite.domain.member.MemberAuthority;
import NestNet.NestNetWebSite.dto.response.attendance.MonthlyAttendanceStatisticsDto;
import NestNet.NestNetWebSite.dto.response.attendance.WeeklyAttendanceStatisticsDto;
import NestNet.NestNetWebSite.exception.CustomException;
import NestNet.NestNetWebSite.exception.ErrorCode;
import NestNet.NestNetWebSite.repository.attachedfile.AttachedFileRepository;
import NestNet.NestNetWebSite.repository.attendance.AttendanceRepository;
import NestNet.NestNetWebSite.repository.member.MemberRepository;
import NestNet.NestNetWebSite.service.attachedfile.AttachedFileService;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AttendanceServiceTest extends TestSupport {

    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown(){
        attendanceRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("사용자가 출석체크를 한다. 이때, 해당일 0시부터 23시59분59초까지 가능하다.")
    @Test
    void saveAttendance() {
        // given
        Member saveMember = memberRepository.save(createMember("test", "테스트"));
        LocalDateTime pastAttandanceTime = LocalDateTime.of(2024, 06, 11, 23, 59, 59);
        attendanceRepository.save(new Attendance(saveMember, pastAttandanceTime));

        LocalDateTime currAttandanceTime = LocalDateTime.of(2024, 06, 12, 0, 0, 0);

        // when
        attendanceService.saveAttendance(saveMember.getLoginId(), currAttandanceTime);

        // then
        LocalDateTime startOfDay = currAttandanceTime.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = currAttandanceTime.toLocalDate().atTime(LocalTime.MAX);
        Optional<Attendance> findAttendance = attendanceRepository.findByMemberAndDay(saveMember, startOfDay, endOfDay);

        Assertions.assertThat(findAttendance).isPresent();
    }

    @DisplayName("사용자가 당일에 이미 출석체크를 했는데 출석체크 시도를 하면 예외가 발생한다.")
    @Test
    void saveAttendanceWhenAlreadyAttended() {
        // given
        Member saveMember = memberRepository.save(createMember("test", "테스트"));
        LocalDateTime pastAttandanceTime = LocalDateTime.of(2024, 06, 12, 0, 0, 0);
        attendanceRepository.save(new Attendance(saveMember, pastAttandanceTime));

        LocalDateTime currAttandanceTime = LocalDateTime.of(2024, 06, 12, 23, 59, 59);

        // when & then
        Assertions.assertThatThrownBy(() -> attendanceService.saveAttendance(saveMember.getLoginId(), currAttandanceTime))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALREADY_ATTENDED);
    }

    @DisplayName("출석체크한 회원을 찾을 수 없는 경우 예외가 발생한다.")
    @Test
    void saveAttendanceWithNoExistMember() {
        // given
        Member saveMember = memberRepository.save(createMember("test", "테스트"));
        LocalDateTime pastAttandanceTime = LocalDateTime.of(2024, 06, 11, 23, 59, 59);
        attendanceRepository.save(new Attendance(saveMember, pastAttandanceTime));

        LocalDateTime currAttandanceTime = LocalDateTime.of(2024, 06, 12, 0, 0, 0);

        String notExistMemberLoginId = "no";

        // when & then
        Assertions.assertThatThrownBy(() -> attendanceService.saveAttendance(notExistMemberLoginId, currAttandanceTime))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_LOGIN_ID_NOT_FOUND);
    }

    @DisplayName("해당 주(월~일)의 top 5 회원 출석 통계를 조회한다.")
    @Test
    void findWeeklyAttendanceStatistics() {
        // given
        Member member1 = createMember("test1", "member1");
        Member member2 = createMember("test2", "member2");
        Member member3 = createMember("test3", "member3");
        Member member4 = createMember("test4", "member4");
        Member member5 = createMember("test5", "member5");
        Member member6 = createMember("test6", "member6");

        memberRepository.saveAll(List.of(member1, member2, member3, member4, member5, member6));

        Attendance attendance1 = new Attendance(member1, LocalDateTime.of(2024, 5, 1, 0, 0, 0));    // 측정안됨
        Attendance attendance2 = new Attendance(member1, LocalDateTime.of(2024, 6, 1, 0, 0, 0));
        Attendance attendance3 = new Attendance(member1, LocalDateTime.of(2024, 6, 11, 0, 0, 0));
        Attendance attendance4 = new Attendance(member1, LocalDateTime.of(2024, 6, 12, 0, 0, 0));
        Attendance attendance5 = new Attendance(member1, LocalDateTime.of(2024, 6, 14, 0, 0, 0));

        Attendance attendance6 = new Attendance(member2, LocalDateTime.of(2024, 6, 13, 0, 0, 0));
        Attendance attendance7 = new Attendance(member2, LocalDateTime.of(2024, 6, 16, 23, 59, 59));

        Attendance attendance8 = new Attendance(member3, LocalDateTime.of(2024, 6, 14, 0, 0, 0));

        Attendance attendance9 = new Attendance(member4, LocalDateTime.of(2024, 6, 15, 0, 0, 0));

        Attendance attendance10 = new Attendance(member5, LocalDateTime.of(2024, 6, 1, 0, 0, 0));   // 측정안됨

        attendanceRepository.saveAll(List.of(attendance1, attendance2, attendance3, attendance4, attendance5,
                attendance6, attendance7, attendance8, attendance9, attendance10));

        LocalDateTime checkTime = LocalDateTime.of(2024, 6, 12, 0, 0, 0);

        // when
        List<WeeklyAttendanceStatisticsDto> weeklyAttendanceStatistics = attendanceService.findWeeklyAttendanceStatistics(checkTime);

        // then
        Assertions.assertThat(weeklyAttendanceStatistics).hasSize(4)
                .extracting("memberName", "type", "point")
                .containsExactlyInAnyOrder(
                        Tuple.tuple(member1.getName(), "weekly", 30L),
                        Tuple.tuple(member2.getName(), "weekly", 20L),
                        Tuple.tuple(member3.getName(), "weekly", 10L),
                        Tuple.tuple(member4.getName(), "weekly", 10L)
                );
    }

    @DisplayName("해당 월의 top 5 회원 출석 통계를 조회한다.")
    @Test
    void findMonthlyAttendanceStatistics() {
        // given
        Member member1 = createMember("test1", "member1");
        Member member2 = createMember("test2", "member2");
        Member member3 = createMember("test3", "member3");
        Member member4 = createMember("test4", "member4");
        Member member5 = createMember("test5", "member5");
        Member member6 = createMember("test6", "member6");

        memberRepository.saveAll(List.of(member1, member2, member3, member4, member5, member6));

        Attendance attendance1 = new Attendance(member1, LocalDateTime.of(2024, 5, 31, 23, 59, 59));    // 측정안됨
        Attendance attendance2 = new Attendance(member1, LocalDateTime.of(2024, 6, 11, 0, 0, 0));
        Attendance attendance3 = new Attendance(member1, LocalDateTime.of(2024, 6, 12, 0, 0, 0));
        Attendance attendance4 = new Attendance(member1, LocalDateTime.of(2024, 6, 14, 0, 0, 0));

        Attendance attendance5 = new Attendance(member2, LocalDateTime.of(2024, 6, 5, 0, 0, 0));
        Attendance attendance6 = new Attendance(member2, LocalDateTime.of(2024, 6, 16, 23, 59, 59));

        Attendance attendance7 = new Attendance(member3, LocalDateTime.of(2024, 6, 8, 0, 0, 0));

        Attendance attendance8 = new Attendance(member4, LocalDateTime.of(2024, 6, 14, 0, 0, 0));

        Attendance attendance9 = new Attendance(member5, LocalDateTime.of(2024, 6, 1, 0, 0, 0));

        Attendance attendance10 = new Attendance(member6, LocalDateTime.of(2024, 6, 1, 0, 0, 0));
        Attendance attendance11 = new Attendance(member6, LocalDateTime.of(2024, 6, 30, 23, 59, 59));

        attendanceRepository.saveAll(List.of(attendance1, attendance2, attendance3, attendance4,
                attendance5, attendance6, attendance7, attendance8, attendance9, attendance10, attendance11));

        LocalDateTime checkTime = LocalDateTime.of(2024, 6, 30, 23, 59, 59); // June 2024

        // when
        List<MonthlyAttendanceStatisticsDto> monthlyAttendanceStatistics = attendanceService.findMonthlyAttendanceStatistics(checkTime);

        // then
        Assertions.assertThat(monthlyAttendanceStatistics).hasSize(5)
                .extracting("memberName", "type", "point")
                .containsExactlyInAnyOrder(
                        Tuple.tuple(member1.getName(), "monthly", 30L),
                        Tuple.tuple(member2.getName(), "monthly", 20L),
                        Tuple.tuple(member3.getName(), "monthly", 10L),
                        Tuple.tuple(member4.getName(), "monthly", 10L),
                        Tuple.tuple(member6.getName(), "monthly", 20L)
                );
    }


    private static Member createMember(String loginId, String name){
        return Member.builder()
                .loginId(loginId)
                .loginPassword("test")
                .name(name)
                .graduated(false)
                .graduateYear(0)
                .studentId("2018000000")
                .grade(4)
                .emailAddress("test@test.com")
                .memberAuthority(MemberAuthority.GENERAL_MEMBER)
                .joinDate(LocalDateTime.of(2022, 06, 12, 10, 0))
                .build();
    }


}