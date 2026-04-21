# BMembresApi

All URIs are relative to *https://api.federation-agricole.mg/v1*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**changerCollectiviteMembre**](BMembresApi.md#changerCollectiviteMembre) | **PATCH** /membres/{id_membre}/changer-collectivite | Changer un membre de collectivité |
| [**createMembre**](BMembresApi.md#createMembre) | **POST** /membres | Admettre un nouveau membre (procédure renforcée) |
| [**demissionnerMembre**](BMembresApi.md#demissionnerMembre) | **PATCH** /membres/{id_membre}/demissionner | Démissionner un membre |


<a id="changerCollectiviteMembre"></a>
# **changerCollectiviteMembre**
> changerCollectiviteMembre(idMembre, changerCollectiviteMembreRequest)

Changer un membre de collectivité

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.BMembresApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.federation-agricole.mg/v1");
    
    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    BMembresApi apiInstance = new BMembresApi(defaultClient);
    Integer idMembre = 56; // Integer | 
    ChangerCollectiviteMembreRequest changerCollectiviteMembreRequest = new ChangerCollectiviteMembreRequest(); // ChangerCollectiviteMembreRequest | 
    try {
      apiInstance.changerCollectiviteMembre(idMembre, changerCollectiviteMembreRequest);
    } catch (ApiException e) {
      System.err.println("Exception when calling BMembresApi#changerCollectiviteMembre");
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
| **idMembre** | **Integer**|  | |
| **changerCollectiviteMembreRequest** | [**ChangerCollectiviteMembreRequest**](ChangerCollectiviteMembreRequest.md)|  | |

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
| **200** | Collectivité changée avec succès |  -  |

<a id="createMembre"></a>
# **createMembre**
> Membre createMembre(membreCreationRequest)

Admettre un nouveau membre (procédure renforcée)

**Nouvelles règles d’admission (21/04/2026)** : - Minimum 2 parrains confirmés - Nombre de parrains de la collectivité cible ≥ nombre de parrains des autres collectivités - Indiquer la nature de la relation avec chaque parrain - Payer 50 000 MGA + l’intégralité des cotisations annuelles obligatoires de la collectivité 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.BMembresApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.federation-agricole.mg/v1");
    
    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    BMembresApi apiInstance = new BMembresApi(defaultClient);
    MembreCreationRequest membreCreationRequest = new MembreCreationRequest(); // MembreCreationRequest | 
    try {
      Membre result = apiInstance.createMembre(membreCreationRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling BMembresApi#createMembre");
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
| **membreCreationRequest** | [**MembreCreationRequest**](MembreCreationRequest.md)|  | |

### Return type

[**Membre**](Membre.md)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Membre admis avec succès |  -  |
| **400** | Conditions de parrainage ou paiement non respectées |  -  |

<a id="demissionnerMembre"></a>
# **demissionnerMembre**
> demissionnerMembre(idMembre)

Démissionner un membre

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.BMembresApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.federation-agricole.mg/v1");
    
    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    BMembresApi apiInstance = new BMembresApi(defaultClient);
    Integer idMembre = 56; // Integer | 
    try {
      apiInstance.demissionnerMembre(idMembre);
    } catch (ApiException e) {
      System.err.println("Exception when calling BMembresApi#demissionnerMembre");
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
| **idMembre** | **Integer**|  | |

### Return type

null (empty response body)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Membre démissionné avec succès |  -  |

