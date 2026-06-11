package com.projekat.biblioteka.service;

import com.projekat.biblioteka.model.Member;
import com.projekat.biblioteka.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }

    public Member createMember(Member member) {
        if (member.getEmail() == null || !member.getEmail().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new RuntimeException("Email adresa nije ispravna");
        }
        if (member.getPhoneNumber() == null || !member.getPhoneNumber().matches("^\\+?[0-9]{8,15}$")) {
            throw new RuntimeException("Broj telefona nije ispravan");
        }
        if (member.getFirstName() == null || member.getFirstName().isBlank()) {
            throw new RuntimeException("Ime člana ne može biti prazno");
        }
        if (member.getLastName() == null || member.getLastName().isBlank()) {
            throw new RuntimeException("Prezime člana ne može biti prazno");
        }
        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new RuntimeException("Član sa email adresom " + member.getEmail() + " već postoji");
        }

        member.setMembershipDate(LocalDate.now());
        member.setActive(true);
        return memberRepository.save(member);
    }

    public Member updateMember(Long id, Member updated) {
        Member existing = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Član sa ID " + id + " nije pronađen"));
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setPhoneNumber(updated.getPhoneNumber());
        return memberRepository.save(existing);
    }

    public void deactivateMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Član sa ID " + id + " nije pronađen"));
        member.setActive(false);
        memberRepository.save(member);
    }
}
