package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberJpaRepositoryTest {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Test
    @Transactional
    public void testMember() throws Exception {
        // given
        Member member = Member.builder()
                .username("memberA")
                .build();

        // when
        Member save = memberJpaRepository.save(member);
        Member findMember = memberJpaRepository.find(save.getId());

        // then
        assertThat(save.getUsername()).isEqualTo("memberA");
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo("memberA");
    }

}