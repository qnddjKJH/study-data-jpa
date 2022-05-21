package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

//    권장하지 않음
//    @GetMapping("/members/{id}")
//    public String findMember(@PathVariable("id") Member member) {
//        return member.getUsername();
//    }

    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5) Pageable pageable) {
        // Page : 결과 정보, Pageable : 파라미터 정보
        // 기본적으로 page 조건을 붙이면 20개씩 불러온다(기본값)
//        return memberRepository.findAll(pageable);

        return memberRepository.findAll(pageable).map(MemberDto::new);
    }

    @PostConstruct
    public void init() {
        for (int i = 1; i <= 100; i++) {
            memberRepository.save(Member.builder().username("user" + i)
                    .age(i)
                    .build());
        }
    }
}
