package NestNet.NestNetWebSite.repository.manager;

import NestNet.NestNetWebSite.TestSupport;
import NestNet.NestNetWebSite.domain.manager.MemberSignUpManagement;
import NestNet.NestNetWebSite.domain.member.Member;
import NestNet.NestNetWebSite.domain.member.MemberAuthority;
import NestNet.NestNetWebSite.repository.member.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

class MemberSignUpManagementRepositoryTest extends TestSupport {

    @Autowired
    private MemberSignUpManagementRepository memberSignUpManagementRepository;

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown(){
        memberSignUpManagementRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("회원가입 요청 PK를 통해 회원가입 요청을 단건 조회한다.")
    @Test
    void findById() {
        // given
        Member member = memberRepository.save(new Member());

        MemberSignUpManagement memberSignUpManagement = memberSignUpManagementRepository.save(createMemberSignUpManagement(member));

        // when
        Optional<MemberSignUpManagement> findSignUpMenagement = memberSignUpManagementRepository.findById(memberSignUpManagement.getId());

        // then
        Assertions.assertThat(findSignUpMenagement).isPresent();
    }

    @DisplayName("모든 회원가입 요청을 조회한다.")
    @Test
    @Transactional          // assertThat에서 getMember로 회원을 가져올 때, 트랜잭션이 없어, Member는 프록시 객체로 조회됨. -> 전체에 트랜잭션 걸어주어 해결
    void findAll() {
        // given
        Member member1 = createMember("member1");
        Member member2 = createMember("member2");

        memberRepository.saveAll(List.of(member1, member2));

        MemberSignUpManagement signUpManagement1 = createMemberSignUpManagement(member1);
        MemberSignUpManagement signUpManagement2 = createMemberSignUpManagement(member2);

        memberSignUpManagementRepository.saveAll(List.of(signUpManagement1, signUpManagement2));

        // when
        List<MemberSignUpManagement> findSignUpManagement = memberSignUpManagementRepository.findAll();

        // then
        Assertions.assertThat(findSignUpManagement).hasSize(2)
                .extracting(signUpManagement -> signUpManagement.getMember().getName())
                .containsExactlyInAnyOrder("member1", "member2");
    }

    @DisplayName("특정 회원의 회원가입 요청을 조회한다.")
    @Test
    @Transactional
    void findByMember() {
        // given
        Member member = memberRepository.save(createMember("member1"));

        MemberSignUpManagement signUpManagement = createMemberSignUpManagement(member);
        memberSignUpManagementRepository.save(signUpManagement);

        // when
        Optional<MemberSignUpManagement> findSignUpManagement = memberSignUpManagementRepository.findByMember(member);

        // then
        Assertions.assertThat(findSignUpManagement).isPresent();
        Assertions.assertThat(findSignUpManagement.get().getMember().getName()).isEqualTo(member.getName());
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

    private static MemberSignUpManagement createMemberSignUpManagement(Member member){
        return MemberSignUpManagement.builder()
                .member(member)
                .requestMemberAuthority(MemberAuthority.GENERAL_MEMBER)
                .build();
    }

}