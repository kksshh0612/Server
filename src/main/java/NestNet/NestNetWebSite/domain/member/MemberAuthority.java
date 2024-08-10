package NestNet.NestNetWebSite.domain.member;

import lombok.Getter;

/**
 * 회원 권한 ENUM
 */
@Getter
public enum MemberAuthority {

    ADMIN("최고관리자"),
    PRESIDENT("회장"),
    VICE_PRESIDENT("부회장"),
    MANAGER("서버관리자"),
    GENERAL_MEMBER("재학생"),
    ON_LEAVE_MEMBER("휴학생"),
    GRADUATED_MEMBER("졸업생"),
    WAITING_FOR_APPROVAL("승인대기"),
    WITHDRAWN_MEMBER("탈퇴");

    private String name;

    MemberAuthority(String name) {
        this.name = name;
    }
}

