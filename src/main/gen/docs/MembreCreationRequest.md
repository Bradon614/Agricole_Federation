

# MembreCreationRequest


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**nom** | **String** |  |  |
|**prenoms** | **String** |  |  |
|**dateNaissance** | **LocalDate** |  |  |
|**genre** | [**GenreEnum**](#GenreEnum) |  |  |
|**adresse** | **String** |  |  |
|**metier** | **String** |  |  |
|**telephone** | **String** |  |  |
|**email** | **String** |  |  |
|**dateAdhesion** | **LocalDate** |  |  [optional] |
|**idCollectivite** | **Integer** |  |  |
|**parrains** | [**List&lt;ParrainInfo&gt;**](ParrainInfo.md) |  |  |
|**cotisationAnnuelle** | [**MembreCreationRequestCotisationAnnuelle**](MembreCreationRequestCotisationAnnuelle.md) |  |  [optional] |



## Enum: GenreEnum

| Name | Value |
|---- | -----|
| M | &quot;M&quot; |
| F | &quot;F&quot; |



