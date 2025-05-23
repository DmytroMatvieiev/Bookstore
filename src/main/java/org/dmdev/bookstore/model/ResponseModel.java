package org.dmdev.bookstore.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseModel {
    public static final String SUCCESS_STATUS = "success";
    public static final String FAIL_STATUS = "fail";
    private String status;
    private String message;
    private Object data;

}
