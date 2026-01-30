package entities.people;

import enums.MemberStatus;

import java.time.LocalDate;

public class Member {
    private final int id;
    private String name;
    private String email;
    private LocalDate membershipDate;
    private MemberStatus status;
    private String phoneNumber;

    public Member(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.membershipDate = LocalDate.now();
        this.status = MemberStatus.ACTIVE;
        this.phoneNumber = "";
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getMembershipDate() {
        return membershipDate;
    }

    public void setMembershipDate(LocalDate membershipDate) {
        this.membershipDate = membershipDate;
    }

    public MemberStatus getStatus() {
        return status;
    }

    public void setStatus(MemberStatus status) {
        this.status = status;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
