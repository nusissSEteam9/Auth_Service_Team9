package nus.iss.se.team9.auth_service_team9.model;

import java.util.ArrayList;
import java.util.List;

public class AddIngredientForm {
	private List<String> ingredientNames;
	private List<Integer> selectedIngredients;
	
	public AddIngredientForm() {
		ingredientNames = new ArrayList<>();
		selectedIngredients = new ArrayList<>();
	}

	public List<String> getIngredientNames() {
		return ingredientNames;
	}

	public void setIngredientNames(List<String> ingredientNames) {
		this.ingredientNames = ingredientNames;
	}

	public List<Integer> getSelectedIngredients() {
		return selectedIngredients;
	}

	public void setSelectedIngredients(List<Integer> selectedIngredients) {
		this.selectedIngredients = selectedIngredients;
	}
}
