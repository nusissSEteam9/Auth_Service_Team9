package nus.iss.se.team9.auth_service_team9.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class IngredientInfo {
	private List<Ingredient> ingredients;
	
	public IngredientInfo() {
    }

	public List<Ingredient> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<Ingredient> ingredients) {
		this.ingredients = ingredients;
	}
	
	public static class Ingredient {
        private String text;
        private List<Parsed> parsed;

        // Getters and setters for text and parsed

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public List<Parsed> getParsed() {
            return parsed;
        }

        public void setParsed(List<Parsed> parsed) {
            this.parsed = parsed;
        }
    }

    public static class Parsed {
        private double quantity;
        private String measure;
        private String foodMatch;
        private Nutrients nutrients;
        private String status;

        public double getQuantity() {
            return quantity;
        }

        public void setQuantity(double quantity) {
            this.quantity = quantity;
        }

		public String getMeasure() {
			return measure;
		}

		public void setMeasure(String measure) {
			this.measure = measure;
		}
        
		public String getFoodMatch() {
			return foodMatch;
		}

		public void setFoodMatch(String foodMatch) {
			this.foodMatch = foodMatch;
		}

		public Nutrients getNutrients() {
			return nutrients;
		}

		public void setNutrients(Nutrients nutrients) {
			this.nutrients = nutrients;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}
    }

    public static class Nutrients {
    	@JsonProperty("ENERC_KCAL")
        private NutrientItem ENERC_KCAL;
    	@JsonProperty("PROCNT")
        private NutrientItem PROCNT;
    	@JsonProperty("CHOCDF")
        private NutrientItem CHOCDF;
    	@JsonProperty("SUGAR")
        private NutrientItem SUGAR;
    	@JsonProperty("NA")
        private NutrientItem NA;
    	@JsonProperty("FAT")
        private NutrientItem FAT;
    	@JsonProperty("FASAT")
        private NutrientItem FASAT;
        

		public NutrientItem getENERC_KCAL() {
			return ENERC_KCAL;
		}

		public void setENERC_KCAL(NutrientItem ENERC_KCAL) {
			this.ENERC_KCAL = ENERC_KCAL;
		}

		public NutrientItem getPROCNT() {
			return PROCNT;
		}

		public void setPROCNT(NutrientItem pROCNT) {
			PROCNT = pROCNT;
		}

		public NutrientItem getCHOCDF() {
			return CHOCDF;
		}

		public void setCHOCDF(NutrientItem cHOCDF) {
			CHOCDF = cHOCDF;
		}

		public NutrientItem getSUGAR() {
			return SUGAR;
		}

		public void setSUGAR(NutrientItem sUGAR) {
			SUGAR = sUGAR;
		}

		public NutrientItem getNA() {
			return NA;
		}

		public void setNA(NutrientItem nA) {
			NA = nA;
		}

		public NutrientItem getFAT() {
			return FAT;
		}

		public void setFAT(NutrientItem fAT) {
			FAT = fAT;
		}

		public NutrientItem getFASAT() {
			return FASAT;
		}

		public void setFASAT(NutrientItem fASAT) {
			FASAT = fASAT;
		}

    }
    
    public static class NutrientItem {
        private String label;
        private double quantity;
        private String unit;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public double getQuantity() {
            return quantity;
        }

        public void setQuantity(Double quantity) {
            this.quantity = quantity;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }
    }
}
