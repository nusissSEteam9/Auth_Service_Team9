package nus.iss.se.team9.auth_service_team9.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Recipe {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column
	@NotBlank(message = "Name is required")
	private String name;

	@Column(length = 800)
	private String description;

	@Column
	private Double rating;

	@Column
	private Integer numberOfSaved;

	@Column
	private Integer numberOfRating;

	@Column
	@NotNull(message = "Preparation Time is required")
	private Integer preparationTime;

	@Column
	@NotNull(message = "Servings is required")
	private Integer servings;

	@Column
	private Integer numberOfSteps;

	@Column
	private Integer healthScore;

	@Column
	private String notes;

	@Column
	private String image;

	@Enumerated(EnumType.STRING)
	private Status status;

	@Column
	private Double calories;

	@Column
	private Double protein;

	@Column
	private Double carbohydrate;

	@Column
	private Double sugar;

	@Column
	private Double sodium;

	@Column
	private Double fat;

	@Column
	private Double saturatedFat;

	@Column
	private LocalDate submittedDate;

	@ElementCollection
	@NotEmpty(message = "At least 1 step is required")
	@Column(length = 500)
	private List<String> steps;

	@ManyToMany(mappedBy = "recipes")
	@JsonManagedReference
	private List<Ingredient> ingredients;

	@ElementCollection
	private List<String> tags;

	@OneToMany(mappedBy = "recipe")
	@JsonManagedReference
	private List<Review> reviews;

	@OneToMany(mappedBy = "recipeReported")
	private List<RecipeReport> recipesToReport;

	@ManyToOne
	@JoinColumn(name = "member_id")
	@JsonBackReference
	private Member member;

	@ManyToMany
	@JoinTable(
			name = "recipe_members_who_save",
			joinColumns = @JoinColumn(name = "saved_recipes_id"),
			inverseJoinColumns = @JoinColumn(name = "members_who_save_id")
	)
	@JsonBackReference // Backward serialization for membersWhoSave to avoid circular reference
	private List<Member> membersWhoSave;

	public Recipe() {
		ingredients = new ArrayList<>();
		tags = new ArrayList<>();
		reviews = new ArrayList<>();
		recipesToReport = new ArrayList<>();
		numberOfSaved = 0;
		numberOfRating = 0;
		rating = 0.0;
		submittedDate = LocalDate.now();
	}

	public Recipe(String name, String description, Member member) {
		this();
		this.name = name;
		this.description = description;
		this.member = member;
		status = Status.PUBLIC;
		steps = new ArrayList<>();
	}

	public Recipe(String name, String description, double rating, int preparationTime, int servings,
				  int numberOfSteps, Member member, double calories, double protein, double carbohydrate, double sugar,
				  double sodium, double fat, double saturatedFat, List<String> steps) {
		this(name, description, member);
		this.rating = rating;
		this.numberOfSaved = 0;
		this.preparationTime = preparationTime;
		this.servings = servings;
		this.numberOfSteps = numberOfSteps;
		this.calories = calories;
		this.protein = protein;
		this.carbohydrate = carbohydrate;
		this.sugar = sugar;
		this.sodium = sodium;
		this.fat = fat;
		this.saturatedFat = saturatedFat;
		this.steps = steps;
		this.healthScore = calculateHealthScore();
		this.status = Status.PUBLIC;
		this.numberOfRating = 0;
	}

    public int calculateHealthScore() {
		healthScore = 0;
		if (protein >= 10 && protein <= 15)
			healthScore++;
		if (carbohydrate >= 55 && carbohydrate <= 75)
			healthScore++;
		if (sugar < 10)
			healthScore++;
		if (sodium < 33)
			healthScore++;
		if (fat >= 15 && fat <= 30)
			healthScore++;
		if (saturatedFat < 10)
			healthScore++;
		return healthScore;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Recipe recipe = (Recipe) o;
		return Objects.equals(id, recipe.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public @NotBlank(message = "Name is required") String getName() {
		return name;
	}

	public void setName(@NotBlank(message = "Name is required") String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public Integer getNumberOfSaved() {
		return numberOfSaved;
	}

	public void setNumberOfSaved(Integer numberOfSaved) {
		this.numberOfSaved = numberOfSaved;
	}

	public Integer getNumberOfRating() {
		return numberOfRating;
	}

	public void setNumberOfRating(Integer numberOfRating) {
		this.numberOfRating = numberOfRating;
	}

	public @NotNull(message = "Preparation Time is required") Integer getPreparationTime() {
		return preparationTime;
	}

	public void setPreparationTime(@NotNull(message = "Preparation Time is required") Integer preparationTime) {
		this.preparationTime = preparationTime;
	}

	public @NotNull(message = "Servings is required") Integer getServings() {
		return servings;
	}

	public void setServings(@NotNull(message = "Servings is required") Integer servings) {
		this.servings = servings;
	}

	public Integer getNumberOfSteps() {
		return numberOfSteps;
	}

	public void setNumberOfSteps(Integer numberOfSteps) {
		this.numberOfSteps = numberOfSteps;
	}

	public Integer getHealthScore() {
		return healthScore;
	}

	public void setHealthScore(Integer healthScore) {
		this.healthScore = healthScore;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Double getProtein() {
		return protein;
	}

	public void setProtein(Double protein) {
		this.protein = protein;
	}

	public Double getCalories() {
		return calories;
	}

	public void setCalories(Double calories) {
		this.calories = calories;
	}

	public Double getCarbohydrate() {
		return carbohydrate;
	}

	public void setCarbohydrate(Double carbohydrate) {
		this.carbohydrate = carbohydrate;
	}

	public Double getSugar() {
		return sugar;
	}

	public void setSugar(Double sugar) {
		this.sugar = sugar;
	}

	public Double getSodium() {
		return sodium;
	}

	public void setSodium(Double sodium) {
		this.sodium = sodium;
	}

	public Double getFat() {
		return fat;
	}

	public void setFat(Double fat) {
		this.fat = fat;
	}

	public Double getSaturatedFat() {
		return saturatedFat;
	}

	public void setSaturatedFat(Double saturatedFat) {
		this.saturatedFat = saturatedFat;
	}

	public LocalDate getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(LocalDate submittedDate) {
		this.submittedDate = submittedDate;
	}

	public @NotEmpty(message = "At least 1 step is required") List<String> getSteps() {
		return steps;
	}

	public void setSteps(@NotEmpty(message = "At least 1 step is required") List<String> steps) {
		this.steps = steps;
	}

	public List<Ingredient> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<Ingredient> ingredients) {
		this.ingredients = ingredients;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}

	public List<RecipeReport> getRecipesToReport() {
		return recipesToReport;
	}

	public void setRecipesToReport(List<RecipeReport> recipesToReport) {
		this.recipesToReport = recipesToReport;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public List<Member> getMembersWhoSave() {
		return membersWhoSave;
	}

	public void setMembersWhoSave(List<Member> membersWhoSave) {
		this.membersWhoSave = membersWhoSave;
	}
}
