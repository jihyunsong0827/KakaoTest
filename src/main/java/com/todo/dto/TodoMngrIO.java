package com.todo.dto;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

@Entity
@Table(name = "TODO_LIST")
@AllArgsConstructor
@Data
@Setter
public class TodoMngrIO {
	
	@Id
	@Column(name = "JOB_ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long jobId;
	
	@Column(name = "REF_JOB_ID")
	private String refJobId;
	
	@Column(name = "JOB_NAME", nullable=false)
	private String jobName;
	
	@Column(name = "COMPLETE_YN", nullable=false, columnDefinition = "number(255) default 0")
	private int completeYN;
	
	@Column(name = "FRST_REG_TIMESTAMP", nullable=false, updatable = false)
	@CreationTimestamp
	private LocalDateTime frstRegTimeStamp;
	
	@Column(name = "LAST_CHNG_TIMESTAMP")
    @UpdateTimestamp
	private LocalDateTime lastChngTimeStamp;
	
	public TodoMngrIO() {
		// No default constructor for entity
	}
	
	@Builder
	public TodoMngrIO(String jobName, String refJobId) {
		this.jobName = jobName;
		this.refJobId = refJobId;
	}

	public Long getJobId() {
		return jobId;
	}

	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}

	public String getRefJobId() {
		return refJobId;
	}

	public void setRefJobId(String refJobId) {
		this.refJobId = refJobId;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public int getCompleteYN() {
		return completeYN;
	}

	public void setCompleteYN(int completeYN) {
		this.completeYN = completeYN;
	}

	public LocalDateTime getFrstRegTimeStamp() {
		return frstRegTimeStamp;
	}

	public void setFrstRegTimeStamp(LocalDateTime frstRegTimeStamp) {
		this.frstRegTimeStamp = frstRegTimeStamp;
	}

	public LocalDateTime getLastChngTimeStamp() {
		return lastChngTimeStamp;
	}

	public void setLastChngTimeStamp(LocalDateTime lastChngTimeStamp) {
		this.lastChngTimeStamp = lastChngTimeStamp;
	}

	@Override
	public String toString() {
		return "ToDoMngrIO [jobId=" + jobId + ", refJobId=" + refJobId + ", jobName=" + jobName + ", CompleteYN="
				+ completeYN + ", frstRegTimeStamp=" + frstRegTimeStamp + ", lastChngTimeStamp=" + lastChngTimeStamp
				+ "]";
	}
}
