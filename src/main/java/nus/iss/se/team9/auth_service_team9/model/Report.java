package nus.iss.se.team9.auth_service_team9.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
public abstract class Report {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	@ManyToOne
	private Member member;
	@Column
	@NotBlank(message = "Reason is required")
	private String reason;
	@Enumerated(EnumType.STRING)
	private Status status;
	
	// getter and setter
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Member getMember() {
		return member;
	}
	public void setMember(Member member) {
		this.member = member;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	
}
