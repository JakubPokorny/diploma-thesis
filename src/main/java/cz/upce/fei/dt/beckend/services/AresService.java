package cz.upce.fei.dt.beckend.services;

import com.google.gson.Gson;
import cz.upce.fei.dt.beckend.dto.AresErrorResponse;
import cz.upce.fei.dt.beckend.dto.AresResponse;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class AresService {

    public static AresResponse searchByICO(String ico) throws Exception{
        Gson gson = new Gson();
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI("https://ares.gov.cz/ekonomicke-subjekty-v-be/rest/ekonomicke-subjekty/" + ico))
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> httpResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() != 200){
            AresErrorResponse error = gson.fromJson(httpResponse.body(), AresErrorResponse.class);
            throw new Exception(error.getKod() + " " +error.getPopis());
        }
        return gson.fromJson(httpResponse.body(), AresResponse.class);
    }
}
