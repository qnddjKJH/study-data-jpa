package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    // 메소드 이름 방식
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age); // 길어지면 답이 없음...

    // @Query(name = "Member.findByUsername") // NamedQuery 방식
    List<Member> findByUsername(@Param("username") String username); // 불편하고, 직접 쿼리 작성 방식이 너무 강력함

    @Query("select m from Member m where m.username = :username and m.age = :age")
        // 직접 쿼리 정의 실무에서 많이 사용
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, m.team.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    // 반환타입을 유연하게 사용할 수 있다.
    List<Member> findListByUsername(String username);   // 컬렉션 조회

    Member findMemberByUsername(String username);   // 단건 (1건임이 보장되면 가능)

    Optional<Member> findOptionalByUsername(String username);   // 단건 Optional

    Page<Member> findByAge(int age, Pageable pageable);

    // 이게 없으면 다른 쿼리들 처럼 ResultList 나 SingleResult 로 실행시킨다
    // @Modifying 이 있다면 executeUpdate 를 사용
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);
}
