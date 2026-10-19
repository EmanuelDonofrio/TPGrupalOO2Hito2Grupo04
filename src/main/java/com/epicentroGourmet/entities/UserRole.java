package com.epicentroGourmet.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
@Table(name = "user_role")
public class UserRole {

	public enum RolEnum{
		ROLE_ADMIN,
		ROLE_RESPONSABLE,
		ROLE_EMPLEADO
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idUserRole;

	@OneToMany(mappedBy = "userRole", fetch = FetchType.LAZY)
	private Set<User> users = new HashSet<>();

	@Enumerated(EnumType.STRING)
	@Column(name="role", nullable=false, length=100)
	private RolEnum role;

	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

	public UserRole(int id, RolEnum role) {
		this.idUserRole = id;
		this.role = role;
	}
}
