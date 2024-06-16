package NestNet.NestNetWebSite.repository.attendance;

import NestNet.NestNetWebSite.TestSupport;
import NestNet.NestNetWebSite.domain.attendance.Attendance;
import NestNet.NestNetWebSite.domain.member.Member;
import NestNet.NestNetWebSite.domain.member.MemberAuthority;
import NestNet.NestNetWebSite.repository.member.MemberRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

class AttendanceRepositoryTest extends TestSupport {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        attendanceRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("회원의 당일 출석 여부를 조회한다.")
    @Test
    void findByMemberAndDay() {
        // given
        Member member = memberRepository.save(new Member());

        LocalDateTime attendanceTime = LocalDateTime.of(2024, 6, 12, 10, 0);
        Attendance attendance = createAttendance(member, attendanceTime);

        attendanceRepository.save(attendance);

        LocalDateTime startOfTheDay = LocalDateTime.of(2024, 6, 12, 0, 0);
        LocalDateTime endOfTheDay = LocalDateTime.of(2024, 6, 12, 12, 59, 59);

        // when
        Optional<Attendance> findAttendance = attendanceRepository.findByMemberAndDay(member, startOfTheDay, endOfTheDay);

        // then
        Assertions.assertThat(findAttendance).isPresent();
    }

    @DisplayName("일주일 동안의 회원들의 출석 통계를 조회한다.")
    @Test
    void findWeeklyStatisticsByMember() {
        // given
        Member member1 = memberRepository.save(createMember("member1"));
        Member member2 = memberRepository.save(createMember("member2"));
        Member member3 = memberRepository.save(createMember("member3"));

        LocalDateTime attendanceTime1 = LocalDateTime.of(2024, 6, 1, 10, 0);        // 다른 주
        LocalDateTime attendanceTime2 = LocalDateTime.of(2024, 6, 10, 0, 0);
        LocalDateTime attendanceTime3 = LocalDateTime.of(2024, 6, 13, 10, 0);
        LocalDateTime attendanceTime4 = LocalDateTime.of(2024, 6, 14, 10, 0);
        LocalDateTime attendanceTime5 = LocalDateTime.of(2024, 6, 16, 11, 59);
        Attendance attendance1 = createAttendance(member1, attendanceTime1);
        Attendance attendance2 = createAttendance(member2, attendanceTime2);
        Attendance attendance3 = createAttendance(member2, attendanceTime3);
        Attendance attendance4 = createAttendance(member2, attendanceTime4);
        Attendance attendance5 = createAttendance(member3, attendanceTime5);

        attendanceRepository.saveAll(List.of(attendance1, attendance2, attendance3, attendance4, attendance5));

        LocalDateTime startOfTheWeek = LocalDateTime.of(2024, 6, 10, 0, 0);
        LocalDateTime endOfTheWeek = LocalDateTime.of(2024, 6, 16, 12, 59, 59);

        // when
        List<Object[]> weeklyStatistics = attendanceRepository.findWeeklyStatisticsByMember(startOfTheWeek, endOfTheWeek);

        // then
        Assertions.assertThat(weeklyStatistics).hasSize(2)
                .extracting(array -> ((Member) array[0]).getName(), array -> array[1])
                .containsExactlyInAnyOrder(
                        Tuple.tuple("member2", 3L),
                        Tuple.tuple("member3", 1L)
                );
    }

    @DisplayName("한 달 동안의 회원들의 출석 통계를 조회한다.")
    @Test
    void findMonthlyStatisticsByMember() {
        // given
        Member member1 = memberRepository.save(createMember("member1"));
        Member member2 = memberRepository.save(createMember("member2"));
        Member member3 = memberRepository.save(createMember("member3"));

        LocalDateTime attendanceTime1 = LocalDateTime.of(2024, 5, 31, 11, 59, 59);        // 다른 월
        LocalDateTime attendanceTime2 = LocalDateTime.of(2024, 6, 1, 0, 0);
        LocalDateTime attendanceTime3 = LocalDateTime.of(2024, 6, 13, 10, 0);
        LocalDateTime attendanceTime4 = LocalDateTime.of(2024, 6, 14, 10, 0);
        LocalDateTime attendanceTime5 = LocalDateTime.of(2024, 6, 30, 11, 59, 59);
        Attendance attendance1 = createAttendance(member1, attendanceTime1);
        Attendance attendance2 = createAttendance(member2, attendanceTime2);
        Attendance attendance3 = createAttendance(member2, attendanceTime3);
        Attendance attendance4 = createAttendance(member2, attendanceTime4);
        Attendance attendance5 = createAttendance(member3, attendanceTime5);

        attendanceRepository.saveAll(List.of(attendance1, attendance2, attendance3, attendance4, attendance5));

        LocalDateTime startOfTheMonth = LocalDateTime.of(2024, 6, 1, 0, 0);
        LocalDateTime endOfTheMonth = LocalDateTime.of(2024, 6, 30, 11, 59, 59);

        // when
        List<Object[]> monthlyStatistics = attendanceRepository.findMonthlyStatisticsByMember(startOfTheMonth, endOfTheMonth);

        // then
        Assertions.assertThat(monthlyStatistics).hasSize(2)
                .extracting(arr -> ((Member)arr[0]).getName(), arr -> arr[1])
                .containsExactlyInAnyOrder(
                        Tuple.tuple("member2", 3L),
                        Tuple.tuple("member3", 1L)
                );
    }

    private static Member createMember(String name){
        return Member.builder()
                .loginId("test")
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

    private static Attendance createAttendance(Member member, LocalDateTime time){
        return Attendance.builder()
                .member(member)
                .attendanceTime(time)
                .build();
    }

}