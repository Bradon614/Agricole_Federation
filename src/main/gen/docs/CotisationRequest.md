

# CotisationRequest


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**idMembre** | **Integer** |  |  |
|**idCollectivite** | **Integer** |  |  |
|**typeCotisation** | [**TypeCotisationEnum**](#TypeCotisationEnum) |  |  |
|**montant** | **BigDecimal** |  |  |
|**dateVersement** | **LocalDate** |  |  |
|**modePaiement** | [**ModePaiementEnum**](#ModePaiementEnum) |  |  |
|**periode** | **String** |  |  [optional] |
|**idCompte** | **Integer** |  |  |



## Enum: TypeCotisationEnum

| Name | Value |
|---- | -----|
| PERIODIQUE | &quot;Periodique&quot; |
| PONCTUELLE | &quot;Ponctuelle&quot; |



## Enum: ModePaiementEnum

| Name | Value |
|---- | -----|
| ESP_CE | &quot;Espèce&quot; |
| VIREMENT_BANCAIRE | &quot;Virement Bancaire&quot; |
| MOBILE_MONEY | &quot;Mobile Money&quot; |



