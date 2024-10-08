package nus.iss.se.team9.auth_service_team9.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Ingredient {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	@Column
	private String foodText;
	@Column
	private Double protein;
	@Column
	private Double calories;
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
	@ManyToMany
	@JoinTable(
		name = "ingredient_recipes",
		joinColumns = @JoinColumn(name = "recipes_id"),
		inverseJoinColumns = @JoinColumn(name = "ingredients_id")
	)
	@JsonBackReference
	private List<Recipe> recipes;

	public Ingredient() {
		recipes = new ArrayList<>();
	}

	public Ingredient(String foodText, double protein, double calories, double carbohydrate, double sugar, double sodium, double fat, double saturatedFat) {
		this.foodText = foodText;
		this.protein = protein;
		this.calories = calories;
		this.carbohydrate = carbohydrate;
		this.sugar = sugar;
		this.sodium = sodium;
		this.fat = fat;
		this.saturatedFat = saturatedFat;
		recipes = new ArrayList<>();
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getFoodText() {
		return foodText;
	}
	public void setFoodText(String foodText) {
		this.foodText = foodText;
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
	public List<Recipe> getRecipes() {
		return recipes;
	}
	public void setRecipes(List<Recipe> recipes) {
		this.recipes = recipes;
	}

	@Override
	public String toString() {
		return foodText + " (" + id + ") " + protein + ", " + calories + ", " + carbohydrate + ", " + sugar + ", " + sodium + ", " + fat + ", " + saturatedFat;
	}
}
