package NestNet.NestNetWebSite.domain.post.exam;

import lombok.Getter;

/**
 * 중간 / 기말  ENUM
 */
@Getter
public enum ExamType {

    MID("중간고사"),
    FINAL("기말고사");

    private String name;

    ExamType(String name) {
        this.name = name;
    }
}
