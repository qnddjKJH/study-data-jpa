package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext EntityManager entityManager;

    @Test
    public void testMember() throws Exception {
        // given
        Member member = Member.builder().username("memberA").build();

        // when
        Member save = memberRepository.save(member);
        Optional<Member> optional = memberRepository.findById(save.getId());
        Member findMember = optional.get();

        // then
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() throws Exception {
        // given
        Member member1 = Member.builder().username("member1").build();
        Member member2 = Member.builder().username("member2").build();
        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        List<Member> all = memberRepository.findAll();

        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();

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
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        // when
        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("memberB", 15);

        // then
        assertThat(result.get(0).getUsername()).isEqualTo(memberB.getUsername());
        assertThat(result.get(0).getAge()).isEqualTo(memberB.getAge());
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testNamedQuery() throws Exception {
        // given
        Member memberA = Member.builder().username("memberA").age(10).build();
        Member memberB = Member.builder().username("memberB").age(20).build();
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        // when
        List<Member> result = memberRepository.findByUsername("memberA");
        Member findMember = result.get(0);

        // then
        assertThat(findMember).isEqualTo(memberA);
    }

    @Test
    public void testQuery() throws Exception {
        // given
        Member memberA = Member.builder().username("memberA").age(10).build();
        Member memberB = Member.builder().username("memberB").age(20).build();
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        // when
        List<Member> result = memberRepository.findUser("memberA", 10);
        Member findMember = result.get(0);

        // then
        assertThat(findMember).isEqualTo(memberA);
    }

    @Test
    public void findUsernameListTest() throws Exception {
        // giver
        Member memberA = Member.builder().username("memberA").age(10).build();
        Member memberB = Member.builder().username("memberB").age(20).build();
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        // when
        List<String> usernameList = memberRepository.findUsernameList();

        // then
        assertThat(usernameList.size()).isEqualTo(2);
        assertThat(usernameList.get(0)).isEqualTo("memberA");
    }

    @Test
    public void findMemberDto() throws Exception {
        // giver
        Team team = Team.builder().name("team").build();
        teamRepository.save(team);
        Member memberA = Member.builder().username("memberA").age(10).team(team).build();
        memberRepository.save(memberA);

        // when
        List<MemberDto> result = memberRepository.findMemberDto();

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(memberA.getId());
        assertThat(result.get(0).getUsername()).isEqualTo(memberA.getUsername());
        assertThat(result.get(0).getTeamName()).isEqualTo(memberA.getTeam().getName());
    }

    @Test
    public void findByNames() throws Exception {
        // given
        Member memberA = Member.builder().username("memberA").age(10).build();
        Member memberB = Member.builder().username("memberB").age(20).build();
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        // when
        List<Member> result = memberRepository.findByNames(Arrays.asList("memberA", "memberB"));

        // then
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void returnType() throws Exception {
        // given
        Member memberA = Member.builder().username("memberA").age(10).build();
        Member memberB = Member.builder().username("memberB").age(20).build();
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        // when
        List<Member> listType = memberRepository.findListByUsername("memberA"); // 컬렉션 타입
        Member memberType = memberRepository.findMemberByUsername("memberB");   // 단건 타입
        Optional<Member> optionalType = memberRepository.findOptionalByUsername("memberA"); // optional 타입

        // then
        assertThat(listType).isInstanceOf(Collection.class);
        assertThat(memberType).isInstanceOf(Member.class);
        assertThat(optionalType).isInstanceOf(Optional.class);
    }

    @Test
    public void paging() throws Exception {
        // given
        memberRepository.save(Member.builder().username("member1").age(10).build());
        memberRepository.save(Member.builder().username("member2").age(10).build());
        memberRepository.save(Member.builder().username("member3").age(10).build());
        memberRepository.save(Member.builder().username("member4").age(10).build());
        memberRepository.save(Member.builder().username("member5").age(10).build());

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        // 스프링 데이터 JPA 는 페이지를 0부터 시작한다.
        // 정렬 조건 Sort 는 자유(선택)다.

        // when
        Page<Member> memberPage = memberRepository.findByAge(age, pageRequest);
//        Long totalCount = memberRepository.totalCount(age);
        // Page 반환 타입이면 total count 쿼리도 같이 내보내서 totalCount 를 따로 구하지 않아도 된다.!

        // Dto 변경법
        Page<MemberDto> memberDtos = memberPage.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        // then
        assertThat(memberPage.getContent().size()).isEqualTo(3); // 5개 중에 3개
        assertThat(memberPage.getTotalElements()).isEqualTo(5); // == totalCount 이다.
        assertThat(memberPage.getNumber()).isEqualTo(0);
        assertThat(memberPage.getTotalPages()).isEqualTo(2);
        assertThat(memberPage.isFirst()).isTrue();
        assertThat(memberPage.hasNext()).isTrue();
    }

    @Test
    public void bulkUpdate() throws Exception {
        // given
        memberRepository.save(Member.builder().username("member1").age(10).build());
        memberRepository.save(Member.builder().username("member2").age(20).build());
        memberRepository.save(Member.builder().username("member3").age(30).build());
        memberRepository.save(Member.builder().username("member4").age(40).build());
        memberRepository.save(Member.builder().username("member5").age(50).build());

        // when
        int resultCount = memberRepository.bulkAgePlus(20);  // 영향을 받은 row 수가 리턴된다.
        entityManager.flush();
        entityManager.clear();

        // then
        assertThat(resultCount).isEqualTo(4);
    }
}