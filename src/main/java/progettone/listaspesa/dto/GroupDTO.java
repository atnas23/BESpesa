package progettone.listaspesa.dto;

import java.time.LocalDateTime;

/**
 * DTO per rappresentare i dati del gruppo
 * trasferiti tra layer Service ↔ Controller
 */
public class GroupDTO {

	private Long id;
	private String name;
	private String description;
	private boolean deleted;

//	private LocalDateTime createdAt;
//	private Long createdBy;
//	private LocalDateTime modifiedAt;
//	private Long modifiedBy;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
//	public LocalDateTime getCreatedAt() {
//		return createdAt;
//	}
//	public void setCreatedAt(LocalDateTime createdAt) {
//		this.createdAt = createdAt;
//	}
//	public Long getCreatedBy() {
//		return createdBy;
//	}
//	public void setCreatedBy(Long createdBy) {
//		this.createdBy = createdBy;
//	}
//
//	public LocalDateTime getModifiedAt() {
//		return modifiedAt;
//	}
//
//	public void setModifiedAt(LocalDateTime modifiedAt) {
//		this.modifiedAt = modifiedAt;
//	}
//
//	public Long getModifiedBy() {
//		return modifiedBy;
//	}
//
//	public void setModifiedBy(Long modifiedBy) {
//		this.modifiedBy = modifiedBy;
//	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public String toString() {
		return "GroupDTO [id=" + id + ", name=" + name + ", description=" + description +
//				", createdAt=" + createdAt + ", createdBy=" + createdBy +
//				", modifiedAt=" + modifiedAt + ", modifiedBy=" + modifiedBy +
				", deleted=" + deleted + "]";
	}
}
