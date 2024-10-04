package nus.iss.se.team9.auth_service_team9.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Member extends User{
	@Column
	private Double height;
	@Column
	private Double weight;
	@Column
	private Integer age;
	@Column
	private LocalDate birthdate;
	@Column
	private String gender;
	@Column
	private Double calorieIntake;
	@Column
	private LocalDate registrationDate;
	
	@Column
	@Enumerated(EnumType.STRING)
	private Status memberStatus;
	
	@ElementCollection
	private List<String> prefenceList;
	
	public List<String> getPerfenceList() {
		return prefenceList;
	}

	public void setPrefenceList(List<String> prefenceList) {
		this.prefenceList = prefenceList;
	}
	
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	private List<ShoppingListItem> shoppingList;
	
	@ManyToMany(mappedBy = "membersWhoSave")
	private List<Recipe> savedRecipes;
	
	@OneToMany(mappedBy = "member")
	private List<Recipe> addedRecipes;
	
	@OneToMany(mappedBy = "member")
	private List<Review> reviews;
	
	@OneToMany(mappedBy = "member")
	private List<Report> reports;
	
	@OneToMany(mappedBy = "memberReported")
	private List<MemberReport> reportsToMember;
	
	public Member() {}
	
	public Member(String username, String password, double height, double weight, LocalDate birthdate, String gender, String email) {
		super(username, password);
		this.height = height;
		this.weight = weight;
		this.birthdate = birthdate;
		this.age = calculateAge();
		this.gender = gender;
		this.calorieIntake = calculateCalorieIntake();
		this.setEmail(email);
		shoppingList = new ArrayList<>();
		savedRecipes = new ArrayList<>();
		addedRecipes = new ArrayList<>();
		reviews = new ArrayList<>();
		reports = new ArrayList<>();
		reportsToMember = new ArrayList<>();
		this.memberStatus = Status.CREATED;
		this.setRegistrationDate(LocalDate.now());
	}

	public Double getHeight() {
		return height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public LocalDate getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(LocalDate birthdate) {
		this.birthdate = birthdate;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Double getCalorieIntake() {
		return calorieIntake;
	}

	public void setCalorieIntake(Double calorieIntake) {
		this.calorieIntake = calorieIntake;
	}

	public List<ShoppingListItem> getShoppingList() {
		return shoppingList;
	}

	public void setShoppingList(List<ShoppingListItem> shoppingList) {
		this.shoppingList = shoppingList;
	}

	public List<Recipe> getSavedRecipes() {
		return savedRecipes;
	}

	public void setSavedRecipes(List<Recipe> savedRecipes) {
		this.savedRecipes = savedRecipes;
	}

	public List<Recipe> getAddedRecipes() {
		return addedRecipes;
	}

	public void setAddedRecipes(List<Recipe> addedRecipes) {
		this.addedRecipes = addedRecipes;
	}

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}

	public List<Report> getReports() {
		return reports;
	}

	public void setReports(List<Report> reports) {
		this.reports = reports;
	}

	public List<MemberReport> getReportsToMember() {
		return reportsToMember;
	}

	public void setReportsToMember(List<MemberReport> reportsToMember) {
		this.reportsToMember = reportsToMember;
	}
	
	public int calculateAge() {
		LocalDate curDate = LocalDate.now();
		return Period.between(birthdate, curDate).getYears();
	}
	
	public Double calculateCalorieIntake() {
		//Using Harris-Benedict formula to calculate Basal Metabolic Rate
		Double BMR = 0.0;
		if (gender.equals("Male")) {
			BMR = 66 + (13.7 * weight) + (5 * height) - (6.8 * age);
		}
		else {
			BMR = 655 + (9.6 * weight) + (1.8 * height) - (4.7 * age);
		}
		return (Math.round(BMR * 10) / 10.0);
	}
	
	public Status getMemberStatus() {
		return memberStatus;
	}

	public void setMemberStatus(Status memberStatus) {
		this.memberStatus = memberStatus;
	}

	public LocalDate getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(LocalDate registrationDate) {
		this.registrationDate = registrationDate;
	}
	
}
