package NestNet.NestNetWebSite.repository.member;

import NestNet.NestNetWebSite.TestSupport;
import NestNet.NestNetWebSite.domain.member.Member;
import NestNet.NestNetWebSite.domain.member.MemberAuthority;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

class MemberRepositoryTest extends TestSupport {

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown(){
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("회원의 PK를 통해 회원을 단건 조회한다.")
    @Test
    void findById() {
        // given
        Member member = memberRepository.save(createMember("test", "member1", "test@test.com", MemberAuthority.GENERAL_MEMBER));

        // when
        Optional<Member> findMember = memberRepository.findById(member.getId());

        // then
        Assertions.assertThat(findMember).isPresent();
        Assertions.assertThat(findMember.get().getName()).isEqualTo("member1");
    }

    @DisplayName("회원의 로그인 아이디를 통해 회원을 단건 조회한다.")
    @Test
    void findByLoginId() {
        // given
        Member member = memberRepository.save(createMember("test", "member1", "test@test.com", MemberAuthority.GENERAL_MEMBER));

        // when
        Optional<Member> findMember = memberRepository.findByLoginId("test");

        // then
        Assertions.assertThat(findMember).isPresent();
        Assertions.assertThat(findMember.get().getLoginId()).isEqualTo("test");
        Assertions.assertThat(findMember.get().getName()).isEqualTo("member1");
    }

    @DisplayName("회원의 이름과 이메일을 통해 회원을 단건 조회한다.")
    @Test
    void findByNameAndEmail() {
        // given
        Member member = memberRepository.save(createMember("test", "member1", "test@test.com", MemberAuthority.GENERAL_MEMBER));

        // when
        Optional<Member> findMember = memberRepository.findByNameAndEmail("member1", "test@test.com");

        // then
        Assertions.assertThat(findMember).isPresent();
        Assertions.assertThat(findMember.get().getLoginId()).isEqualTo("test");
        Assertions.assertThat(findMember.get().getName()).isEqualTo("member1");
        Assertions.assertThat(findMember.get().getEmailAddress()).isEqualTo("test@test.com");
    }

    @DisplayName("탈퇴멤버, admin, 승인 대기 권한을 가진 회원을 제외한 모든 회원을 조회한다.")
    @Test
    void findAllApprovedMemberExceptAdmin() {
        // given
        Member adminMember = createMember("test-admin", "adminMember", "test@test.com", MemberAuthority.ADMIN);
        Member member1 = createMember("test1", "member1", "test@test.com", MemberAuthority.PRESIDENT);
        Member member2 = createMember("test2", "member2", "test@test.com", MemberAuthority.VICE_PRESIDENT);
        Member member3 = createMember("test3", "member3", "test@test.com", MemberAuthority.MANAGER);
        Member member4 = createMember("test4", "member4", "test@test.com", MemberAuthority.GENERAL_MEMBER);
        Member member5 = createMember("test5", "member5", "test@test.com", MemberAuthority.ON_LEAVE_MEMBER);
        Member member6 = createMember("test6", "member6", "test@test.com", MemberAuthority.GRADUATED_MEMBER);
        Member notApprovedMember = createMember("test7", "member7", "test@test.com", MemberAuthority.WAITING_FOR_APPROVAL);
        Member withdrawnMember = createMember("알수없음", "알수없음", "test@test.com", MemberAuthority.WITHDRAWN_MEMBER);   // 탈퇴 멤버

        memberRepository.saveAll(List.of(adminMember, member1, member2, member3, member4, member5, member6, notApprovedMember, withdrawnMember));

        // when
        List<Member> memberList = memberRepository.findAllApprovedMemberExceptAdmin(
                List.of(MemberAuthority.ADMIN, MemberAuthority.WAITING_FOR_APPROVAL, MemberAuthority.WITHDRAWN_MEMBER)
        );

        // then
        Assertions.assertThat(memberList).hasSize(6)
                .extracting("name", "memberAuthority")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("member1", MemberAuthority.PRESIDENT),
                        Tuple.tuple("member2", MemberAuthority.VICE_PRESIDENT),
                        Tuple.tuple("member3", MemberAuthority.MANAGER),
                        Tuple.tuple("member4", MemberAuthority.GENERAL_MEMBER),
                        Tuple.tuple("member5", MemberAuthority.ON_LEAVE_MEMBER),
                        Tuple.tuple("member6", MemberAuthority.GRADUATED_MEMBER)
                );
    }

    private static Member createMember(String loginId, String name, String email, MemberAuthority memberAuthority){
        return Member.builder()
                .loginId(loginId)
                .loginPassword("test")
                .name(name)
                .graduated(false)
                .graduateYear(0)
                .studentId("2018000000")
                .grade(4)
                .emailAddress(email)
                .memberAuthority(memberAuthority)
                .joinDate(LocalDateTime.of(2022, 06, 12, 10, 0))
                .build();
    }
}