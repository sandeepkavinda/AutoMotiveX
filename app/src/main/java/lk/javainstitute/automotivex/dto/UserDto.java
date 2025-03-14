package lk.javainstitute.automotivex.dto;

public class UserDto {

    private String docId;

    private String email;
    private String first_name;
    private String last_name;
    private String registered_date;
    private String status;
    private String type;

    public UserDto(String docId, String email, String first_name, String last_name, String registered_date, String status, String type) {
        this.docId = docId;
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.registered_date = registered_date;
        this.status = status;
        this.type = type;
    }

    public UserDto() {
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getRegistered_date() {
        return registered_date;
    }

    public void setRegistered_date(String registered_date) {
        this.registered_date = registered_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
