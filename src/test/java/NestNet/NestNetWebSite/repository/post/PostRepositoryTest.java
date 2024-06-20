package NestNet.NestNetWebSite.repository.post;

import NestNet.NestNetWebSite.TestSupport;
import NestNet.NestNetWebSite.domain.member.Member;
import NestNet.NestNetWebSite.domain.member.MemberAuthority;
import NestNet.NestNetWebSite.domain.post.Post;
import NestNet.NestNetWebSite.domain.post.notice.NoticePost;
import NestNet.NestNetWebSite.repository.member.MemberRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

class PostRepositoryTest extends TestSupport {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown(){
        postRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("회원이 작성한 모든 게시물을 조회한다.")
    @Test
    @Transactional
    void findAllByMember() {
        // given
        Member member1 = createMember("member1");
        Member member2 = createMember("member2");

        memberRepository.saveAll(List.of(member1, member2));

        NoticePost post1 = createPost("title1", member1);
        NoticePost post2 = createPost("title2", member1);
        NoticePost post3 = createPost("title3", member2);
        NoticePost post4 = createPost("title4", member2);
        NoticePost post5 = createPost("title5", member2);

        postRepository.saveAll(List.of(post1, post2, post3, post4, post5));

        // when
        List<Post> findPostList = postRepository.findAllByMember(member2);

        // then
        Assertions.assertThat(findPostList).hasSize(3)
                .extracting(post -> post.getTitle(), post -> post.getMember().getName())
                .containsExactlyInAnyOrder(
                        Tuple.tuple("title3", "member2"),
                        Tuple.tuple("title4", "member2"),
                        Tuple.tuple("title5", "member2")
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

    private static NoticePost createPost(String title, Member member){
        return NoticePost.builder()
                .title(title)
                .bodyContent("test")
                .member(member)
                .viewCount(0L)
                .recommendationCount(0)
                .createdTime(LocalDateTime.of(2024, 6, 12, 10, 0))
                .build();
    }

}