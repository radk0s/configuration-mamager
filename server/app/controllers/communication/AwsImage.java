package controllers.communication;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;

@JsonFormat
public class AwsImage implements Serializable {

    private String name;
    private String id;

    public AwsImage(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
