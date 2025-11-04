package progettone.listaspesa.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class User extends GenericEntity {
	
    private String email;
    private String password;
    private String first_name;
    private String last_name;
    private LocalDateTime date_of_birth;
    private Set<Group> groups = new HashSet<>();
    private Set<Role> roles = new HashSet<>();
	
	
	private String createdBy;
	private LocalDateTime createdAt;

	private String modifiedBy;
	private LocalDateTime modifiedAt;
	
	
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
	public String getFirst_name() {
		return first_name;
	}
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	public String getLast_name() {
		return last_name;
	}
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	public LocalDateTime getDate_of_birth() {
		return date_of_birth;
	}
	public void setDate_of_birth(LocalDateTime date_of_birth) {
		this.date_of_birth = date_of_birth;
	}
	public Set<Group> getGroups() {
		return groups;
	}
	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}
	public Set<Role> getRoles() {
		return roles;
	}
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public LocalDateTime getModifiedAt() {
		return modifiedAt;
	}
	public void setModifiedAt(LocalDateTime modifiedAt) {
		this.modifiedAt = modifiedAt;
	}
        
}
