package com.yfaleev.springchat.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ApiResponse {

    private Boolean success;

    private List<String> errors = new ArrayList<>();

    public ApiResponse(boolean success) {
        this.success = success;
    }

    @JsonCreator
    public ApiResponse(@JsonProperty("success") Boolean success, @JsonProperty("errors") List<String> errors) {
        this.success = success;
        this.errors = errors;
    }
}
