package com.projekat.biblioteka.service;

import com.projekat.biblioteka.model.Member;
import com.projekat.biblioteka.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    private Member validMember;

    @BeforeEach
    void setUp() {
        validMember = new Member();
        validMember.setFirstName("Mateja");
        validMember.setLastName("Lazic");
        validMember.setEmail("mateja@test.com");
        validMember.setPhoneNumber("+381601234567");
    }

    @Test
    void testCreateMember_Success() {
        when(memberRepository.existsByEmail("mateja@test.com")).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenReturn(validMember);

        Member result = memberService.createMember(validMember);

        assertNotNull(result);
        assertEquals("Mateja", result.getFirstName());
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void testCreateMember_InvalidEmail_ThrowsException() {
        validMember.setEmail("neispravan-email");
        assertThrows(RuntimeException.class, () -> memberService.createMember(validMember));
        verify(memberRepository, never()).save(any());
    }

    @Test
    void testCreateMember_NullEmail_ThrowsException() {
        validMember.setEmail(null);
        assertThrows(RuntimeException.class, () -> memberService.createMember(validMember));
    }

    @Test
    void testCreateMember_InvalidPhone_ThrowsException() {
        validMember.setPhoneNumber("123");
        assertThrows(RuntimeException.class, () -> memberService.createMember(validMember));
    }

    @Test
    void testCreateMember_EmptyFirstName_ThrowsException() {
        validMember.setFirstName("");
        assertThrows(RuntimeException.class, () -> memberService.createMember(validMember));
    }

    @Test
    void testCreateMember_DuplicateEmail_ThrowsException() {
        when(memberRepository.existsByEmail("mateja@test.com")).thenReturn(true);
        assertThrows(RuntimeException.class, () -> memberService.createMember(validMember));
        verify(memberRepository, never()).save(any());
    }

    @Test
    void testGetAllMembers() {
        when(memberRepository.findAll()).thenReturn(List.of(validMember));
        List<Member> result = memberService.getAllMembers();
        assertEquals(1, result.size());
        verify(memberRepository).findAll();
    }

    @Test
    void testDeactivateMember_NotFound_ThrowsException() {
        when(memberRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> memberService.deactivateMember(99L));
    }

    @Test
    void testDeactivateMember_Success() {
        validMember.setActive(true);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(validMember));
        when(memberRepository.save(any())).thenReturn(validMember);

        memberService.deactivateMember(1L);

        assertFalse(validMember.isActive());
        verify(memberRepository).save(validMember);
    }
}
