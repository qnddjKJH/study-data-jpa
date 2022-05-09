package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Test
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

    @Test
    public void basicCRUD() throws Exception {
        // given
        Member member1 = Member.builder().username("member1").build();
        Member member2 = Member.builder().username("member2").build();
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        // when
        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();

        List<Member> all = memberJpaRepository.findAll();

        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        long deletedCount = memberJpaRepository.count();

        // then
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        assertThat(all.size()).isEqualTo(2);

        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThenTest() throws Exception {
        // given
        Member memberA = Member.builder().username("memberA").age(10).build();
        Member memberB = Member.builder().username("memberB").age(20).build();
        memberJpaRepository.save(memberA);
        memberJpaRepository.save(memberB);

        // when
        List<Member> result = memberJpaRepository.findByUsernameAndAgeGreaterThan("memberB", 15);

        // then
        assertThat(result.get(0).getUsername()).isEqualTo(memberA.getUsername());
        assertThat(result.get(0).getAge()).isEqualTo(memberA.getAge());
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testNamedQuery() throws Exception {
        // given
        Member memberA = Member.builder().username("memberA").age(10).build();
        Member memberB = Member.builder().username("memberB").age(20).build();
        memberJpaRepository.save(memberA);
        memberJpaRepository.save(memberB);

        // when
        List<Member> result = memberJpaRepository.findByUsername("memberA");
        Member findMember = result.get(0);

        // then
        assertThat(findMember).isEqualTo(memberA);
    }

    @Test
    public void paging() throws Exception {
        // given
        memberJpaRepository.save(Member.builder().username("member1").age(10).build());
        memberJpaRepository.save(Member.builder().username("member2").age(20).build());
        memberJpaRepository.save(Member.builder().username("member3").age(30).build());
        memberJpaRepository.save(Member.builder().username("member4").age(40).build());
        memberJpaRepository.save(Member.builder().username("member5").age(50).build());

        int age = 10;
        int offset = 0;
        int limit = 3;

        // when
        List<Member> result = memberJpaRepository.findByPage(age, offset, limit);
        Long totalCount = memberJpaRepository.totalCount(age);

        // then
        assertThat(result.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(5);
    }

    @Test
    public void bulkUpdate() throws Exception {
        // given
        memberJpaRepository.save(Member.builder().username("member1").age(10).build());
        memberJpaRepository.save(Member.builder().username("member2").age(20).build());
        memberJpaRepository.save(Member.builder().username("member3").age(30).build());
        memberJpaRepository.save(Member.builder().username("member4").age(40).build());
        memberJpaRepository.save(Member.builder().username("member5").age(50).build());

        // when
        int resultCount = memberJpaRepository.bulkAgePlus(20);  // 영향을 받은 row 수가 리턴된다.

        // then
        assertThat(resultCount).isEqualTo(4);
    }
}