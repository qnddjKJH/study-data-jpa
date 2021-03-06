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
        List<Member> listType = memberRepository.findListByUsername("memberA"); // ????????? ??????
        Member memberType = memberRepository.findMemberByUsername("memberB");   // ?????? ??????
        Optional<Member> optionalType = memberRepository.findOptionalByUsername("memberA"); // optional ??????

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
        // ????????? ????????? JPA ??? ???????????? 0?????? ????????????.
        // ?????? ?????? Sort ??? ??????(??????)???.

        // when
        Page<Member> memberPage = memberRepository.findByAge(age, pageRequest);
//        Long totalCount = memberRepository.totalCount(age);
        // Page ?????? ???????????? total count ????????? ?????? ???????????? totalCount ??? ?????? ????????? ????????? ??????.!

        // Dto ?????????
        Page<MemberDto> memberDtos = memberPage.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        // then
        assertThat(memberPage.getContent().size()).isEqualTo(3); // 5??? ?????? 3???
        assertThat(memberPage.getTotalElements()).isEqualTo(5); // == totalCount ??????.
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
        int resultCount = memberRepository.bulkAgePlus(20);  // ????????? ?????? row ?????? ????????????.
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
        // N+1 ?????? ??????
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

        entityManager.flush();  // ?????? ????????? ???????????? ????????? ??????????????? ????????????.(1??? ??????)
        entityManager.clear();  // ????????? ???????????? ?????? ????????????.

        // when
        Member findMember = memberRepository.findById(member1.getId()).get();// ??????????????? ?????? ?????? ????????? ?????????.
        findMember.changeName("member2");

        entityManager.flush(); // ???????????? ????????? ????????? ?????? ?????? ??? ??? ??????. (??????||?????? ?????? : ?????? ??????)
        // ?????? - ????????? ????????? ??????. (????????? ???????????? ???)
        // ????????? 2?????? ???????????? ?????? ??????. == ????????? ??? ?????? ??????.
        // ?????? ????????? ?????? ????????? ????????? ??? ?????? ?????? ?????? ??????????????????. 
        // ????????? ????????? ????????? ???????????????.
        entityManager.clear();

        // ??????????????? ????????? ?????????????????? ???????????? ????????? ??? ???????????? ?????? ?????? ?????????
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