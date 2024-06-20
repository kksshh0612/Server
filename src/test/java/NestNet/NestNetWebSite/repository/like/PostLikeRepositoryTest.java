package NestNet.NestNetWebSite.repository.like;

import NestNet.NestNetWebSite.TestSupport;
import NestNet.NestNetWebSite.domain.like.PostLike;
import NestNet.NestNetWebSite.domain.member.Member;
import NestNet.NestNetWebSite.domain.post.Post;
import NestNet.NestNetWebSite.domain.post.notice.NoticePost;
import NestNet.NestNetWebSite.repository.member.MemberRepository;
import NestNet.NestNetWebSite.repository.post.NoticePostRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

class PostLikeRepositoryTest extends TestSupport {

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private NoticePostRepository noticePostRepository;

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown(){
        postLikeRepository.deleteAllInBatch();
        noticePostRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("어떤 회원이 어떤 게시물에 좋아요를 눌렀는지 조회한다.")
    @Test
    void findByMemberAndPost() {
        // given
        Member member = memberRepository.save(new Member());
        NoticePost post = noticePostRepository.save(new NoticePost());

        postLikeRepository.save(createPostLike(member, post));

        // when
        Optional<PostLike> findPostLike = postLikeRepository.findByMemberAndPost(member, post);

        // then
        Assertions.assertThat(findPostLike).isPresent();
    }

    private static PostLike createPostLike(Member member, Post post){
        return PostLike.builder()
                .post(post)
                .member(member)
                .build();
    }

}