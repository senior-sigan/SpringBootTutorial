package it.sevenbits.springboottutorial.web.domain;

public class CommonResponse {
    private String error = null;
    private Boolean success = false;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
