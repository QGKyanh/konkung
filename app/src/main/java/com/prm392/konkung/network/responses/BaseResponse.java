package com.prm392.konkung.network.responses;

import com.google.gson.annotations.SerializedName;

public class BaseResponse<T> {
    @SerializedName("statusCode")
    private int statusCode;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private T data;

    // Constructors
    public BaseResponse() {}

    // Getters and Setters
    public int getStatusCode() { return statusCode; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public boolean isSuccess() {
        return statusCode == 200 && "Success".equals(status);
    }
}
