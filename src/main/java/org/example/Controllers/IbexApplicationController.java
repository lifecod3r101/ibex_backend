package org.example.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.RequestBody;
import okhttp3.*;
import org.example.Misc.IbexApplicationCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class IbexApplicationController {
    private IbexApplicationCredentials ibexApplicationCredentials = new IbexApplicationCredentials();


    OkHttpClient client = new OkHttpClient();

    @GetMapping("/getAccount/{accountId}/{authorizationKey}")
    public ResponseEntity<?> getAccountDetails(@PathVariable("accountId") String accountId, @PathVariable("authorizationKey") String authorizationKey) {
        Request request = new Request.Builder()
                .url(ibexApplicationCredentials.getV2baseUrl().concat("account/").concat(accountId))
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", authorizationKey)
                .build();

        try {
            Response response = client.newCall(request).execute();
            return ResponseEntity.ok(response.body().string());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @PostMapping("/createInvoice/{authorizationKey}")
    public ResponseEntity<?> createInvoice(@RequestParam("accountId") String accountCreatorId, @RequestParam("amount") String invoiceAmount, @PathVariable("authorizationKey") String authorizationKey) {
        MediaType mediaType = MediaType.parse("application/json");
        Map<String, Object> stuff = new HashMap<>();
        stuff.put("expiration", 900);
        stuff.put("accountId", accountCreatorId);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String stuffString = objectMapper.writeValueAsString(stuff);
            RequestBody body = RequestBody.create(mediaType, stuffString);
            Request request = new Request.Builder()
                    .url(ibexApplicationCredentials.getV2baseUrl().concat("invoice/add"))
                    .post(body)
                    .addHeader("accept", "application/json")
                    .addHeader("content-type", "application/json")
                    .addHeader("Authorization", authorizationKey)
                    .build();
            Response response = client.newCall(request).execute();
            return ResponseEntity.ok(response.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @PostMapping("/payInvoice/{authorizationKey}")
    public ResponseEntity<?> payInvoice(@RequestParam("accountId") String accountPayId, @RequestParam("bolt11") String bolt11, @PathVariable("authorizationKey") String authorizationKey) {
        Request request = new Request.Builder()
                .url(ibexApplicationCredentials.getV2baseUrl().concat("invoice/pay"))
                .post(null)
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .addHeader("Authorization", authorizationKey)
                .build();

        try {
            Response response = client.newCall(request).execute();
            return ResponseEntity.ok(response.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/getInvoice/{bolt11InvoiceId}/{authorizationKey}")
    public ResponseEntity<?> getInvoiceInformation(@PathVariable("bolt11InvoiceId") String bolt11InvoiceId, @PathVariable("authorizationKey") String authorizationKey) {
        Request request = new Request.Builder()
                .url(ibexApplicationCredentials.getBaseUrl().concat("invoice/from-bolt11").concat("/").concat(bolt11InvoiceId))
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", authorizationKey)
                .build();

        try {
            Response response = client.newCall(request).execute();
            return ResponseEntity.ok(response.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/createAccount/{accountName}/{authorizationKey}")
    public ResponseEntity<?> createAccount(@PathVariable("accountName") String accountName, @PathVariable("authorizationKey") String authorizationKey) {
        MediaType mediaType = MediaType.parse("application/json");
        Map<String, Object> stuff = new HashMap<>();
        stuff.put("expiration", 900);
        stuff.put("name", accountName);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String stuffString = objectMapper.writeValueAsString(stuff);
            RequestBody body = RequestBody.create(mediaType, stuffString);
            Request request = new Request.Builder()
                    .url(ibexApplicationCredentials.getBaseUrl().concat("account/create"))
                    .post(body)
                    .addHeader("accept", "application/json")
                    .addHeader("content-type", "application/json")
                    .addHeader("Authorization", authorizationKey)
                    .build();
            Response response = client.newCall(request).execute();
            assert response.body() != null;
            return ResponseEntity.ok(response.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
