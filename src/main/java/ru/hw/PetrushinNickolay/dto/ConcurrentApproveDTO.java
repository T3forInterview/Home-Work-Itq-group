package ru.hw.PetrushinNickolay.dto;

public class ConcurrentApproveDTO {
    private int totalAttempts;
    private int successApproves;
    private int conflicts;
    private int errors;
    private String status;

    public ConcurrentApproveDTO() {
    }

    public ConcurrentApproveDTO(int totalAttempts, int successApproves, int conflicts, int errors, String status) {
        this.totalAttempts = totalAttempts;
        this.successApproves = successApproves;
        this.conflicts = conflicts;
        this.errors = errors;
        this.status = status;
    }

    public int getTotalAttempts() {
        return totalAttempts;
    }

    public void setTotalAttempts(int totalAttempts) {
        this.totalAttempts = totalAttempts;
    }

    public int getSuccessApproves() {
        return successApproves;
    }

    public void setSuccessApproves(int successApproves) {
        this.successApproves = successApproves;
    }

    public int getConflicts() {
        return conflicts;
    }

    public void setConflicts(int conflicts) {
        this.conflicts = conflicts;
    }

    public int getErrors() {
        return errors;
    }

    public void setErrors(int errors) {
        this.errors = errors;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
