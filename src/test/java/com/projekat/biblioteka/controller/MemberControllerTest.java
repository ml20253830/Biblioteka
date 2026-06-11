package com.projekat.biblioteka.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projekat.biblioteka.model.Member;
import com.projekat.biblioteka.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import static org.mockito.ArgumentMatchers.any;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    private Member testMember;

    @BeforeEach
    void setUp() {
        testMember = new Member();
        testMember.setId(1L);
        testMember.setFirstName("Mateja");
        testMember.setLastName("Lazic");
        testMember.setEmail("matejatest@gmail.com");
        testMember.setPhoneNumber("+381601234567");
        testMember.setMembershipDate(LocalDate.now());
        testMember.setActive(true);
    }

    @Test
    void testGetAllMembers_ReturnsOk() throws Exception {
        when(memberService.getAllMembers()).thenReturn(List.of(testMember));

        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Mateja"));
    }

    @Test
    void testGetMemberById_Found_ReturnsOk() throws Exception {
        when(memberService.getMemberById(1L)).thenReturn(Optional.of(testMember));

        mockMvc.perform(get("/api/members/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("matejatest@gmail.com"));
    }

    @Test
    void testGetMemberById_NotFound_Returns404() throws Exception {
        when(memberService.getMemberById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/members/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateMember_ReturnsOk() throws Exception {
        when(memberService.createMember(any(Member.class))).thenReturn(testMember);

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMember)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Mateja"));
    }

    @Test
    void testDeactivateMember_ReturnsNoContent() throws Exception {
        mockMvc.perform(patch("/api/members/1/deactivate"))
                .andExpect(status().isNoContent());
    }
}
