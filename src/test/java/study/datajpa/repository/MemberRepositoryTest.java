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

    @Test
    public void findMemberLazy() throws Exception {
        //given
        //member1 -> teamA
        //member2 -> teamB
        Team teamA = teamRepository.save(Team.builder().name("teamA").build());
        Team teamB = teamRepository.save(Team.builder().name("teamB").build());
        memberRepository.save(Member.builder().username("member1").age(10).team(teamA).build());
        memberRepository.save(Member.builder().username("member2").age(20).team(teamB).build());

        entityManager.flush();
        entityManager.clear();
        // N+1 문제 발생
        List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }

        entityManager.flush();
        entityManager.clear();
        // fetch join : with EntityGraph
        List<Member> fetchMembers = memberRepository.findByMembersEntityGraph();
        for (Member member : fetchMembers) {
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint() throws Exception {
        Member member1 = memberRepository.save(Member.builder().username("member1").age(10).build());

        entityManager.flush();  // 실제 쿼리가 날라가고 영속성 컨텍스트에 남아있다.(1차 캐시)
        entityManager.clear();  // 영속성 컨텍스트 전부 날라간다.

        // when
        Member findMember = memberRepository.findById(member1.getId()).get();// 실무에서는 절대 그냥 꺼내면 안된다.
        findMember.changeName("member2");

        entityManager.flush(); // 업데이트 쿼리가 나가는 것을 확인 할 수 있다. (상태||변경 감지 : 더티 체킹)
        // 단점 - 원본이 있어야 한다. (영속성 컨텍스트 안)
        // 데이터 2개를 관리하는 거랑 같다. == 비용이 더 많이 든다.
        // 나는 조회만 하고 싶은데 비용이 더 많이 드는 순간 비효율적이다. 
        // 그래서 힌트를 주도록 만들어졌다.
        entityManager.clear();

        // 내부적으로 변경이 불가능하다고 생각하고 스냅샷 등 변경감지 등을 하지 않는다
        Member readOnlyMember = memberRepository.findReadOnlyByUsername("member2"); // 
        readOnlyMember.changeName("member1");

        entityManager.flush();
    }

    @Test
    public void lock() throws Exception {
        // given
        Member member1 = memberRepository.save(Member.builder().username("member1").age(10).build());

        entityManager.flush();
        entityManager.clear();

        // when
        List<Member> result = memberRepository.findLockByUsername("member1");

        // then
    }

    @Test
    public void callCustom() throws Exception {
        // given
        List<Member> memberCustom = memberRepository.findMemberCustom();
        // when

        // then
    }
}