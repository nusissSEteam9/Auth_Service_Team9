package nus.iss.se.team9.auth_service_team9.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class MemberReport extends Report {
	@ManyToOne
	private Member memberReported;
	
	//getter and setter
	public Member getMemberReported() {
		return memberReported;
	}

	public void setMemberReported(Member memberReported) {
		this.memberReported = memberReported;
	}
	
}
