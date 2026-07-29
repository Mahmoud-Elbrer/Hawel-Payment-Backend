package com.hawel.identity_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {

	private Long id;

	@NotEmpty
	@Size(min = 2, message = "name should hava at least 2 characters")
	private String name;

	@NotEmpty
	private String username;

	@Email(message = "email should not be empty")
	private String email;

	@NotEmpty
	private String password;

}
