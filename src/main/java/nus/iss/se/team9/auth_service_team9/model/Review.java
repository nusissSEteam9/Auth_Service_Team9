package nus.iss.se.team9.auth_service_team9.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Review {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column
	private Integer rating;
	@Column(length = 1200)
	private String comment;
	@Column
	private LocalDate reviewDate;
	@ManyToOne
	private Member member;
	@ManyToOne
	private Recipe recipe;
	
	public Review() {
		setReviewDate(LocalDate.now());
	}
	
	public Review(int rating, String comment, Member member, Recipe recipe) {
		this.rating = rating;
		this.comment = comment;
		this.member = member;
		this.recipe = recipe;
	}
	
	// getter and setter
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getRating() {
		return rating;
	}
	public void setRating(Integer rating) {
		this.rating = rating;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Member getMember() {
		return member;
	}
	public void setMember(Member member) {
		this.member = member;
	}
	public Recipe getRecipe() {
		return recipe;
	}
	public void setRecipe(Recipe recipe) {
		this.recipe = recipe;
	}
	public LocalDate getReviewDate() {
		return reviewDate;
	}
	public void setReviewDate(LocalDate reviewDate) {
		this.reviewDate = reviewDate;
	}
}
