package com.epicentroGourmet.entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
@Table(name="user_role", uniqueConstraints=@UniqueConstraint(columnNames= {"role", "user_id"}))
public class UserRole {

	public enum RolEnum{
		ROLE_ADMIN,
		ROLE_RESPONSABLE,
		ROLE_EMPLEADO
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idUserRole;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_id", nullable=false)
	private User user;
	@Enumerated(EnumType.STRING)
	@Column(name="role", nullable=false, length=100)
	private RolEnum role;

	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

	public UserRole(int id, User user, RolEnum role) {
		this.idUserRole = id;
		this.user = user;
		this.role = role;
	}
}
