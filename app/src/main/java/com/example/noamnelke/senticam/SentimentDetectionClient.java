package com.example.noamnelke.senticam;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.logging.Logger;

class SentimentDetectionClient {

    private static final String HOST = "http://10.0.2.2:5000"; // this url is for the emulator, change according to need
    private Logger logger = Logger.getLogger(this.getClass().getName());

    private static SentimentDetectionClient ourInstance;
    private final RequestQueue queue;

    private SentimentDetectionClient(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    static SentimentDetectionClient getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new SentimentDetectionClient(context);
        }
        return ourInstance;
    }

    void recognize(Bitmap image, final DetectionResultsHandler resultsHandler) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] imageBinaryData = stream.toByteArray();

        SentimentDetectionRequest request = new SentimentDetectionRequest(Request.Method.POST,
                HOST + "/recognize", imageBinaryData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        logger.info(response.toString());
                        try {
                            byte[] decodedString = Base64.decode(response.getString("imageData"), Base64.DEFAULT);
                            Bitmap image = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            JSONObject faceRectangle = response.getJSONObject("faceRectangle");
                            int x = faceRectangle.getInt("left");
                            int y = faceRectangle.getInt("top");
                            int width = faceRectangle.getInt("width");
                            int height = faceRectangle.getInt("height");
                            Bitmap croppedImage = Bitmap.createBitmap(image, x, y, width, height);
                            String responseText = "Most likely detected sentiment:\n" + response.getString("feeling");
                            resultsHandler.handleResults(responseText, croppedImage);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        logger.warning("request failed");
                    }
                }
        );
        queue.add(request);
        logger.info("request added to queue");
    }

    public interface DetectionResultsHandler {
        void handleResults(String res, Bitmap image);
    }
}
