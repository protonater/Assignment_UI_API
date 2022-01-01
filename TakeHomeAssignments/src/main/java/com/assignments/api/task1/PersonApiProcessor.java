package com.assignments.api.task1;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PersonApiProcessor {
    public static void main(String[] args) {
        PersonApiProcessor personApiProcessor = new PersonApiProcessor();
        try {
            List<String> firstNameList = personApiProcessor.getFirstFiveNames();
            List<JSONObject> details = personApiProcessor.getApiDetails(firstNameList);
            personApiProcessor.printDetails(details);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // step 1: Use https://randomuser.me/api/ and make a list of five names (first name only) from the API Response.
    private List<String> getFirstFiveNames() throws UnirestException {
        List<String> firstNameList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            HttpResponse<String> httpResponse = Unirest.get("https://randomuser.me/api/").asString();
            JSONObject jsonObject = new JSONObject(httpResponse.getBody());
            JSONObject nameObject = new JSONObject(new JSONArray(jsonObject.getJSONArray("results")).getJSONObject(0).toString());
            firstNameList.add(nameObject.getJSONObject("name").get("first").toString());
        }
        return firstNameList;
    }

    // Step 2: Use these apis for each of them
    private List<JSONObject> getApiDetails(List<String> firstNameList) {
        List<JSONObject> details = new ArrayList<>();
        firstNameList.forEach(name -> {
            try {
                HttpResponse<String> ageResp = Unirest.get("https://api.agify.io/?name=" + name).asString();
                HttpResponse<String> genderResp = Unirest.get("https://api.genderize.io/?name=" + name).asString();
                HttpResponse<String> nationResp = Unirest.get("https://api.nationalize.io/?name=" + name).asString();
                JSONObject jsonObject = new JSONObject(nationResp.getBody());

                jsonObject.put("age", new JSONObject(ageResp.getBody()).get("age").toString());
                jsonObject.put("count", new JSONObject(ageResp.getBody()).get("count"));

                jsonObject.put("gender", new JSONObject(genderResp.getBody()).get("gender"));
                jsonObject.put("probability", new JSONObject(genderResp.getBody()).get("probability"));
                jsonObject.put("gender_count", new JSONObject(genderResp.getBody()).get("count"));

                details.add(jsonObject);
            } catch (UnirestException e) {
                e.printStackTrace();
            }
        });
        return details;
    }

    // Step 3: Print the details of each person in console in ascending order by age
    private void printDetails(List<JSONObject> details) {
        details.sort(Comparator.comparingInt(obj -> obj.getString("age").equalsIgnoreCase("null") ? 0 : Integer.parseInt(obj.getString("age"))));
        details.forEach(System.out::println);
    }
}
