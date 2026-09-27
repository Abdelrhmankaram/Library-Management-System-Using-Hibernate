package com.karam.library.service;

import com.karam.library.dao.MemberDao;
import com.karam.library.exception.MemberNotFoundException;
import com.karam.library.model.Member;

import java.util.List;
import java.util.Optional;

public record MemberService(MemberDao memberDao) {

    public void addMember(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null");
        }

        if (member.getName() == null || member.getName().isBlank()) {
            throw new IllegalArgumentException("Member name cannot be empty");
        }

        if (member.getEmail() == null || member.getEmail().isBlank()) {
            throw new IllegalArgumentException("Member email cannot be empty");
        }

        if (memberDao.findByEmail(member.getEmail()).isPresent()) {
            throw new IllegalStateException(
                    "A member with this email already exists"
            );
        }

        memberDao.save(member);
    }

    public Optional<Member> findById(long id) {
        Optional<Member> member = memberDao.findById(id);

        if(member.isEmpty()) {
            throw new MemberNotFoundException("Member with id " + id + " not found");
        }
        return member;
    }

    public List<Member> findAll() {
        return memberDao.findAll();
    }

    public Optional<Member> findByEmail(String email) {
        return memberDao.findByEmail(email);
    }

    public void printMembers(List<Member> members) {
        if (members == null || members.isEmpty()) {
            System.out.println("Members: (none found)");
            return;
        }

        String format = "%-5s %-20s %-30s%n";
        System.out.printf(format, "ID", "Name", "Email");
        System.out.println("-".repeat(55));

        for (Member member : members) {
            System.out.printf(format, member.getId(), member.getName(), member.getEmail());
        }
    }

    public void printMember(Optional<Member> member) {
        member.ifPresentOrElse(
                m -> printMembers(List.of(m)),
                () -> System.out.println("Member: not found")
        );
    }
}