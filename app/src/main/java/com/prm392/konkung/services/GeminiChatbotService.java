package com.prm392.konkung.services;

import com.prm392.konkung.BuildConfig;
import okhttp3.*;
import org.json.*;
import java.io.IOException;

public class GeminiChatbotService {
    // Use BuildConfig for API key from local.properties, fallback to hardcoded for demo
    private static final String GEMINI_API_KEY = BuildConfig.GEMINI_API_KEY;
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + GEMINI_API_KEY;
    
    private OkHttpClient client = new OkHttpClient();
    
    public GeminiChatbotService() {
        // Gemini-only AI chatbot service
    }
    
    public void generateAIResponse(String userMessage, ResponseCallback callback) {
        // Always use Gemini AI
        if (GEMINI_API_KEY != null && !GEMINI_API_KEY.isEmpty() && !"null".equals(GEMINI_API_KEY)) {
            callGemini(userMessage, callback);
        } else {
            String errorMsg = "API key không được cấu hình. ";
            if (GEMINI_API_KEY == null) {
                errorMsg += "GEMINI_API_KEY is null. ";
            } else if (GEMINI_API_KEY.isEmpty()) {
                errorMsg += "GEMINI_API_KEY is empty. ";
            } else if ("null".equals(GEMINI_API_KEY)) {
                errorMsg += "GEMINI_API_KEY is 'null' string. ";
            }
            errorMsg += "Vui lòng thêm GEMINI_API_KEY vào local.properties và rebuild project.";
            callback.onError(errorMsg);
        }
    }
    
    private void callGemini(String userMessage, ResponseCallback callback) {
        String systemPrompt = "Bạn là trợ lý AI thông minh cho cửa hàng sữa chuyên về các sản phẩm từ sữa và dairy. " +
                            "CHỈ trả lời các câu hỏi về sữa, sản phẩm từ sữa, dinh dưỡng, bảo quản, nấu ăn với sữa, " +
                            "và các chủ đề liên quan. Giữ câu trả lời thân thiện, có thông tin hữu ích và dưới 150 từ. " +
                            "LUÔN trả lời bằng tiếng Việt. Nếu được hỏi về các chủ đề không liên quan đến sữa, " +
                            "hãy lịch sự chuyển hướng về các câu hỏi liên quan đến sữa.";
        
        String fullPrompt = systemPrompt + "\n\nCâu hỏi của khách hàng: " + userMessage + "\n\nTrả lời bằng tiếng Việt:";
        
        try {
            // Build JSON request for Gemini
            JSONObject textPart = new JSONObject();
            textPart.put("text", fullPrompt);
            
            JSONArray parts = new JSONArray();
            parts.put(textPart);
            
            JSONObject content = new JSONObject();
            content.put("parts", parts);
            
            JSONArray contents = new JSONArray();
            contents.put(content);
            
            JSONObject generationConfig = new JSONObject();
            generationConfig.put("temperature", 0.7);
            generationConfig.put("topK", 32);
            generationConfig.put("topP", 0.8);
            generationConfig.put("maxOutputTokens", 150);
            
            JSONObject requestBody = new JSONObject();
            requestBody.put("contents", contents);
            requestBody.put("generationConfig", generationConfig);
            
            Request request = new Request.Builder()
                    .url(GEMINI_URL)
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody.toString(), 
                          MediaType.parse("application/json")))
                    .build();
        
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            String responseBody = response.body().string();
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            
                            String aiResponse = jsonResponse.getJSONArray("candidates")
                                    .getJSONObject(0)
                                    .getJSONObject("content")
                                    .getJSONArray("parts")
                                    .getJSONObject(0)
                                    .getString("text");
                            callback.onSuccess(aiResponse.trim());
                        } catch (Exception e) {
                            callback.onError("Lỗi khi xử lý phản hồi từ AI: " + e.getMessage());
                        }
                    } else {
                        callback.onError("AI service không khả dụng. Mã lỗi: " + response.code());
                    }
                }
                
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onError("Không thể kết nối với AI service: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            callback.onError("Lỗi khi tạo yêu cầu AI: " + e.getMessage());
        }
    }

    public interface ResponseCallback {
        void onSuccess(String response);
        void onError(String error);
    }
}
