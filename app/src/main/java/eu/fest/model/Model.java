package eu.fest.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Model implements Serializable{

    private static final long serialVersionUID = 1L;

    private int id;

    /*@SerializedName("message")
    private String message;*/

    private List<String> errors = new ArrayList<String>();

    public boolean isValid() {
        return errors == null || errors.size() == 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /*public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }*/

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getErrorMessage() {
        StringBuilder sb = new StringBuilder();
        for (String e : errors) {
            sb.append(e).append(" ");
        }
        return sb.toString();
    }
}
