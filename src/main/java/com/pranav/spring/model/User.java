package com.pranav.spring.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

/*
 * H2 table for `users`
 * with fields:
 * ID (varchar),
 * USERNAME (varchar),
 * EMAIL (varchar),
 * FIRST_NAME (varchar),
 * LAST_NAME (varchar)
 * */
@Entity
@Table(name = "users",
		uniqueConstraints = {
		@UniqueConstraint(columnNames = "username"),
		@UniqueConstraint(columnNames = "email")
})
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "username")
	@NotBlank
	private String username;

	@Column(name = "email")
	@NotBlank
	private String email;

	@Column(name = "password")
	@NotBlank
	private String password;

	@Column(name = "firstName")
	@NotBlank
	private String firstName;

	@Column(name = "lastName")
	@NotBlank
	private String lastName;

	public User() {

	}

	public User(String username, String email, String password, String firstName, String lastName) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", firstName=" + firstName + ", lastName=" + lastName + "]";
	}

}
