# CCotisationsApi

All URIs are relative to *https://api.federation-agricole.mg/v1*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createCotisation**](CCotisationsApi.md#createCotisation) | **POST** /cotisations | Enregistrer un paiement de cotisation |


<a id="createCotisation"></a>
# **createCotisation**
> createCotisation(cotisationRequest)

Enregistrer un paiement de cotisation

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.CCotisationsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.federation-agricole.mg/v1");
    
    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    CCotisationsApi apiInstance = new CCotisationsApi(defaultClient);
    CotisationRequest cotisationRequest = new CotisationRequest(); // CotisationRequest | 
    try {
      apiInstance.createCotisation(cotisationRequest);
    } catch (ApiException e) {
      System.err.println("Exception when calling CCotisationsApi#createCotisation");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **cotisationRequest** | [**CotisationRequest**](CotisationRequest.md)|  | |

### Return type

null (empty response body)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Cotisation enregistrée |  -  |

