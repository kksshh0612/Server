package NestNet.NestNetWebSite.repository.post;

import NestNet.NestNetWebSite.TestSupport;
import NestNet.NestNetWebSite.domain.member.Member;
import NestNet.NestNetWebSite.domain.post.exam.ExamCollectionPost;
import NestNet.NestNetWebSite.domain.post.exam.ExamType;
import NestNet.NestNetWebSite.repository.member.MemberRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

class ExamCollectionPostRepositoryTest extends TestSupport {

    @Autowired
    private ExamCollectionPostRepository examCollectionPostRepository;

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown(){
        examCollectionPostRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("족보 게시물의 PK를 통해 족보 게시물을 단건 조회한다.")
    @Test
    void findById() {
        // given
        Member member = memberRepository.save(new Member());

        ExamCollectionPost post = examCollectionPostRepository.save(
                createPost(member, LocalDateTime.of(2024, 6, 12, 10, 0),
                        "test", "test", 2022, 1, ExamType.MID)
        );

        // when
        Optional<ExamCollectionPost> findPost = examCollectionPostRepository.findById(post.getId());

        // then
        Assertions.assertThat(findPost.get().getId()).isEqualTo(post.getId());
        Assertions.assertThat(findPost.get().getSubject()).isEqualTo(post.getSubject());
    }

    @DisplayName("필터링 없이 페이징하여 족보 게시물을 조회한다.")
    @Test
    void findAllByFilter1() {
        // given
        Member member = memberRepository.save(new Member());

        LocalDateTime createdTime = LocalDateTime.of(2024, 6, 12, 10, 0);

        ExamCollectionPost post1 = createPost(member, createdTime, "subject1", "professor1", 2022, 1, ExamType.MID);
        ExamCollectionPost post2 = createPost(member, createdTime, "subject1", "professor1", 2022, 1, ExamType.FINAL);
        ExamCollectionPost post3 = createPost(member, createdTime, "subject2", "professor2", 2022, 2, ExamType.MID);
        ExamCollectionPost post4 = createPost(member, createdTime, "subject2", "professor2", 2022, 2, ExamType.FINAL);
        ExamCollectionPost post5 = createPost(member, createdTime, "subject3", "professor3", 2022, 1, ExamType.MID);
        ExamCollectionPost post6 = createPost(member, createdTime, "subject3", "professor3", 2023, 1, ExamType.FINAL);
        ExamCollectionPost post7 = createPost(member, createdTime, "subject4", "professor4", 2023, 2, ExamType.MID);
        ExamCollectionPost post8 = createPost(member, createdTime, "subject4", "professor4", 2023, 2, ExamType.FINAL);
        ExamCollectionPost post9 = createPost(member, createdTime, "subject5", "professor1", 2023, 1, ExamType.MID);
        ExamCollectionPost post10 = createPost(member, createdTime, "subject5", "professor1", 2023, 1, ExamType.FINAL);

        examCollectionPostRepository.saveAll(List.of(post1, post2, post3, post4, post5, post6, post7, post8, post9, post10));

        PageRequest pageRequest = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "id"));

        // when
        List<ExamCollectionPost> findPostList = examCollectionPostRepository.findAllByFilter(null, null, null, null, null, pageRequest).getContent();

        // then
        Assertions.assertThat(findPostList).hasSize(5)
                .extracting("subject", "professor", "examType")
                .containsExactly(
                        Tuple.tuple("subject1", "professor1", ExamType.MID),
                        Tuple.tuple("subject1", "professor1", ExamType.FINAL),
                        Tuple.tuple("subject2", "professor2", ExamType.MID),
                        Tuple.tuple("subject2", "professor2", ExamType.FINAL),
                        Tuple.tuple("subject3", "professor3", ExamType.MID)
                );
    }

    @DisplayName("과목으로 필터링하고 페이징하여 족보 게시물을 조회한다.")
    @Test
    void findAllByFilter2() {
        // given
        Member member = memberRepository.save(new Member());

        LocalDateTime createdTime = LocalDateTime.of(2024, 6, 12, 10, 0);

        ExamCollectionPost post1 = createPost(member, createdTime, "subject1", "professor1", 2022, 1, ExamType.MID);
        ExamCollectionPost post2 = createPost(member, createdTime, "subject1", "professor1", 2022, 1, ExamType.FINAL);
        ExamCollectionPost post3 = createPost(member, createdTime, "subject2", "professor2", 2022, 2, ExamType.MID);
        ExamCollectionPost post4 = createPost(member, createdTime, "subject2", "professor2", 2022, 2, ExamType.FINAL);
        ExamCollectionPost post5 = createPost(member, createdTime, "subject3", "professor3", 2022, 1, ExamType.MID);
        ExamCollectionPost post6 = createPost(member, createdTime, "subject3", "professor3", 2023, 1, ExamType.FINAL);
        ExamCollectionPost post7 = createPost(member, createdTime, "subject4", "professor4", 2023, 2, ExamType.MID);
        ExamCollectionPost post8 = createPost(member, createdTime, "subject4", "professor4", 2023, 2, ExamType.FINAL);
        ExamCollectionPost post9 = createPost(member, createdTime, "subject5", "professor1", 2023, 1, ExamType.MID);
        ExamCollectionPost post10 = createPost(member, createdTime, "subject5", "professor1", 2023, 1, ExamType.FINAL);

        examCollectionPostRepository.saveAll(List.of(post1, post2, post3, post4, post5, post6, post7, post8, post9, post10));

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));

        // when
        List<ExamCollectionPost> findPostList = examCollectionPostRepository.findAllByFilter("subject1", null, null, null, null, pageRequest).getContent();

        // then
        Assertions.assertThat(findPostList).hasSize(2)
                .extracting("subject", "professor", "examType")
                .containsExactly(
                        Tuple.tuple("subject1", "professor1", ExamType.MID),
                        Tuple.tuple("subject1", "professor1", ExamType.FINAL)
                );
    }

    @DisplayName("교수로 필터링하고 페이징하여 족보 게시물을 조회한다.")
    @Test
    void findAllByFilter3() {
        // given
        Member member = memberRepository.save(new Member());

        LocalDateTime createdTime = LocalDateTime.of(2024, 6, 12, 10, 0);

        ExamCollectionPost post1 = createPost(member, createdTime, "subject1", "professor1", 2022, 1, ExamType.MID);
        ExamCollectionPost post2 = createPost(member, createdTime, "subject1", "professor1", 2022, 1, ExamType.FINAL);
        ExamCollectionPost post3 = createPost(member, createdTime, "subject2", "professor2", 2022, 2, ExamType.MID);
        ExamCollectionPost post4 = createPost(member, createdTime, "subject2", "professor2", 2022, 2, ExamType.FINAL);
        ExamCollectionPost post5 = createPost(member, createdTime, "subject3", "professor3", 2022, 1, ExamType.MID);
        ExamCollectionPost post6 = createPost(member, createdTime, "subject3", "professor3", 2023, 1, ExamType.FINAL);
        ExamCollectionPost post7 = createPost(member, createdTime, "subject4", "professor4", 2023, 2, ExamType.MID);
        ExamCollectionPost post8 = createPost(member, createdTime, "subject4", "professor4", 2023, 2, ExamType.FINAL);
        ExamCollectionPost post9 = createPost(member, createdTime, "subject5", "professor1", 2023, 1, ExamType.MID);
        ExamCollectionPost post10 = createPost(member, createdTime, "subject5", "professor1", 2023, 1, ExamType.FINAL);

        examCollectionPostRepository.saveAll(List.of(post1, post2, post3, post4, post5, post6, post7, post8, post9, post10));

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));

        // when
        List<ExamCollectionPost> findPostList = examCollectionPostRepository.findAllByFilter(null, "professor1", null, null, null, pageRequest).getContent();

        // then
        Assertions.assertThat(findPostList).hasSize(4)
                .extracting("subject", "professor", "examType")
                .containsExactly(
                        Tuple.tuple("subject1", "professor1", ExamType.MID),
                        Tuple.tuple("subject1", "professor1", ExamType.FINAL),
                        Tuple.tuple("subject5", "professor1", ExamType.MID),
                        Tuple.tuple("subject5", "professor1", ExamType.FINAL)
                );
    }

    @DisplayName("년도로 필터링하고 페이징하여 족보 게시물을 조회한다.")
    @Test
    void findAllByFilter4() {
        // given
        Member member = memberRepository.save(new Member());

        LocalDateTime createdTime = LocalDateTime.of(2024, 6, 12, 10, 0);

        ExamCollectionPost post1 = createPost(member, createdTime, "subject1", "professor1", 2022, 1, ExamType.MID);
        ExamCollectionPost post2 = createPost(member, createdTime, "subject1", "professor1", 2022, 1, ExamType.FINAL);
        ExamCollectionPost post3 = createPost(member, createdTime, "subject2", "professor2", 2022, 2, ExamType.MID);
        ExamCollectionPost post4 = createPost(member, createdTime, "subject2", "professor2", 2022, 2, ExamType.FINAL);
        ExamCollectionPost post5 = createPost(member, createdTime, "subject3", "professor3", 2022, 1, ExamType.MID);
        ExamCollectionPost post6 = createPost(member, createdTime, "subject3", "professor3", 2023, 1, ExamType.FINAL);
        ExamCollectionPost post7 = createPost(member, createdTime, "subject4", "professor4", 2023, 2, ExamType.MID);
        ExamCollectionPost post8 = createPost(member, createdTime, "subject4", "professor4", 2023, 2, ExamType.FINAL);
        ExamCollectionPost post9 = createPost(member, createdTime, "subject5", "professor1", 2023, 1, ExamType.MID);
        ExamCollectionPost post10 = createPost(member, createdTime, "subject5", "professor1", 2023, 1, ExamType.FINAL);

        examCollectionPostRepository.saveAll(List.of(post1, post2, post3, post4, post5, post6, post7, post8, post9, post10));

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));

        // when
        List<ExamCollectionPost> findPostList = examCollectionPostRepository.findAllByFilter(null, null, 2023, null, null, pageRequest).getContent();

        // then
        Assertions.assertThat(findPostList).hasSize(5)
                .extracting("subject", "professor", "examType")
                .containsExactly(
                        Tuple.tuple("subject3", "professor3", ExamType.FINAL),
                        Tuple.tuple("subject4", "professor4", ExamType.MID),
                        Tuple.tuple("subject4", "professor4", ExamType.FINAL),
                        Tuple.tuple("subject5", "professor1", ExamType.MID),
                        Tuple.tuple("subject5", "professor1", ExamType.FINAL)
                );
    }

    @DisplayName("학기로 필터링하고 페이징하여 족보 게시물을 조회한다.")
    @Test
    void findAllByFilter5() {
        // given
        Member member = memberRepository.save(new Member());

        LocalDateTime createdTime = LocalDateTime.of(2024, 6, 12, 10, 0);

        ExamCollectionPost post1 = createPost(member, createdTime, "subject1", "professor1", 2022, 1, ExamType.MID);
        ExamCollectionPost post2 = createPost(member, createdTime, "subject1", "professor1", 2022, 1, ExamType.FINAL);
        ExamCollectionPost post3 = createPost(member, createdTime, "subject2", "professor2", 2022, 2, ExamType.MID);
        ExamCollectionPost post4 = createPost(member, createdTime, "subject2", "professor2", 2022, 2, ExamType.FINAL);
        ExamCollectionPost post5 = createPost(member, createdTime, "subject3", "professor3", 2022, 1, ExamType.MID);
        ExamCollectionPost post6 = createPost(member, createdTime, "subject3", "professor3", 2023, 1, ExamType.FINAL);
        ExamCollectionPost post7 = createPost(member, createdTime, "subject4", "professor4", 2023, 2, ExamType.MID);
        ExamCollectionPost post8 = createPost(member, createdTime, "subject4", "professor4", 2023, 2, ExamType.FINAL);
        ExamCollectionPost post9 = createPost(member, createdTime, "subject5", "professor1", 2023, 1, ExamType.MID);
        ExamCollectionPost post10 = createPost(member, createdTime, "subject5", "professor1", 2023, 1, ExamType.FINAL);

        examCollectionPostRepository.saveAll(List.of(post1, post2, post3, post4, post5, post6, post7, post8, post9, post10));

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));

        // when
        List<ExamCollectionPost> findPostList = examCollectionPostRepository.findAllByFilter(null, null, null, 1, null, pageRequest).getContent();

        // then
        Assertions.assertThat(findPostList).hasSize(6)
                .extracting("subject", "professor", "examType")
                .containsExactly(
                        Tuple.tuple("subject1", "professor1", ExamType.MID),
                        Tuple.tuple("subject1", "professor1", ExamType.FINAL),
                        Tuple.tuple("subject3", "professor3", ExamType.MID),
                        Tuple.tuple("subject3", "professor3", ExamType.FINAL),
                        Tuple.tuple("subject5", "professor1", ExamType.MID),
                        Tuple.tuple("subject5", "professor1", ExamType.FINAL)
                );
    }

    @DisplayName("타입(중간/기말)로 필터링하고 페이징하여 족보 게시물을 조회한다.")
    @Test
    void findAllByFilter6() {
        // given
        Member member = memberRepository.save(new Member());

        LocalDateTime createdTime = LocalDateTime.of(2024, 6, 12, 10, 0);

        ExamCollectionPost post1 = createPost(member, createdTime, "subject1", "professor1", 2022, 1, ExamType.MID);
        ExamCollectionPost post2 = createPost(member, createdTime, "subject1", "professor1", 2022, 1, ExamType.FINAL);
        ExamCollectionPost post3 = createPost(member, createdTime, "subject2", "professor2", 2022, 2, ExamType.MID);
        ExamCollectionPost post4 = createPost(member, createdTime, "subject2", "professor2", 2022, 2, ExamType.FINAL);
        ExamCollectionPost post5 = createPost(member, createdTime, "subject3", "professor3", 2022, 1, ExamType.MID);
        ExamCollectionPost post6 = createPost(member, createdTime, "subject3", "professor3", 2023, 1, ExamType.FINAL);
        ExamCollectionPost post7 = createPost(member, createdTime, "subject4", "professor4", 2023, 2, ExamType.MID);
        ExamCollectionPost post8 = createPost(member, createdTime, "subject4", "professor4", 2023, 2, ExamType.FINAL);
        ExamCollectionPost post9 = createPost(member, createdTime, "subject5", "professor1", 2023, 1, ExamType.MID);
        ExamCollectionPost post10 = createPost(member, createdTime, "subject5", "professor1", 2023, 1, ExamType.FINAL);

        examCollectionPostRepository.saveAll(List.of(post1, post2, post3, post4, post5, post6, post7, post8, post9, post10));

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));

        // when
        List<ExamCollectionPost> findPostList = examCollectionPostRepository.findAllByFilter(null, null, null, null, ExamType.FINAL, pageRequest).getContent();

        // then
        Assertions.assertThat(findPostList).hasSize(5)
                .extracting("subject", "professor", "examType")
                .containsExactly(
                        Tuple.tuple("subject1", "professor1", ExamType.FINAL),
                        Tuple.tuple("subject2", "professor2", ExamType.FINAL),
                        Tuple.tuple("subject3", "professor3", ExamType.FINAL),
                        Tuple.tuple("subject4", "professor4", ExamType.FINAL),
                        Tuple.tuple("subject5", "professor1", ExamType.FINAL)
                );
    }

    @DisplayName("교수, 년도, 타입(중간/기말)로 필터링하고 페이징하여 족보 게시물을 조회한다.")
    @Test
    void findAllByFilter7() {
        // given
        Member member = memberRepository.save(new Member());

        LocalDateTime createdTime = LocalDateTime.of(2024, 6, 12, 10, 0);

        ExamCollectionPost post1 = createPost(member, createdTime, "subject1", "professor1", 2022, 1, ExamType.MID);
        ExamCollectionPost post2 = createPost(member, createdTime, "subject1", "professor1", 2022, 1, ExamType.FINAL);
        ExamCollectionPost post3 = createPost(member, createdTime, "subject2", "professor2", 2022, 2, ExamType.MID);
        ExamCollectionPost post4 = createPost(member, createdTime, "subject2", "professor2", 2022, 2, ExamType.FINAL);
        ExamCollectionPost post5 = createPost(member, createdTime, "subject3", "professor3", 2022, 1, ExamType.MID);
        ExamCollectionPost post6 = createPost(member, createdTime, "subject3", "professor3", 2023, 1, ExamType.FINAL);
        ExamCollectionPost post7 = createPost(member, createdTime, "subject4", "professor4", 2023, 2, ExamType.MID);
        ExamCollectionPost post8 = createPost(member, createdTime, "subject4", "professor4", 2023, 2, ExamType.FINAL);
        ExamCollectionPost post9 = createPost(member, createdTime, "subject5", "professor1", 2023, 1, ExamType.MID);
        ExamCollectionPost post10 = createPost(member, createdTime, "subject5", "professor1", 2023, 1, ExamType.FINAL);

        examCollectionPostRepository.saveAll(List.of(post1, post2, post3, post4, post5, post6, post7, post8, post9, post10));

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));

        // when
        List<ExamCollectionPost> findPostList = examCollectionPostRepository.findAllByFilter(null, "professor1", 2022, null, ExamType.FINAL, pageRequest).getContent();

        // then
        Assertions.assertThat(findPostList).hasSize(1)
                .extracting("subject", "professor", "examType")
                .containsExactly(
                        Tuple.tuple("subject1", "professor1", ExamType.FINAL)
                );
    }

    private static ExamCollectionPost createPost(Member member, LocalDateTime createdTime,
                                                 String subject, String professor, int year, int semester, ExamType examType){
        return ExamCollectionPost.builder()
                .title("test")
                .bodyContent("test")
                .member(member)
                .viewCount(0L)
                .recommendationCount(0)
                .createdTime(createdTime)
                .subject(subject)
                .professor(professor)
                .year(year)
                .semester(semester)
                .examType(examType)
                .build();
    }
}