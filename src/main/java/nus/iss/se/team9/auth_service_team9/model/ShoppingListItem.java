package nus.iss.se.team9.auth_service_team9.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
@Entity
public class ShoppingListItem {
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

    // Getters and Setters
    @ManyToOne
	@JsonBackReference
	private Member member;

    @Column
	private String ingredientName;

	@Column
	private boolean isChecked;

	// Default constructor
	public ShoppingListItem() {}

	// Constructor with fields
	public ShoppingListItem(Member member, String ingredientName) {
		this.member = member;
		this.ingredientName = ingredientName;
		this.isChecked = false;
	}

    public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
}
