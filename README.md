# Money transfers

##Short description:
Application provides money transfers between accounts.
Import money from system is absent. 
Export money to system only with creating account. 
Base entity is User.
User can have several accounts with amounts.
On each transfer event special record is being created.

##How to run
Just run **_tomcat7:run_**

##REST API
Available swagger by path: ```http://localhost:8080/swagger.json```

API:
```json
{
"swagger":"2.0",
"info":{
"version":"1.0.2"
},
"host":"localhost:8080",
"basePath":"/",
"tags":[
{
"name":"Users"
},
{
"name":"Process"
},
{
"name":"Accounts"
}
],
"schemes":[
"http"
],
"paths":{
"/accounts":{
"get":{
"tags":[
"Accounts"
],
"operationId":"loadAll",
"produces":[
"application/json"
],
"parameters":[
],
"responses":{
"200":{
"description":"successful operation",
"schema":{
"type":"array",
"items":{
"$ref":"#/definitions/AccountDto"
}
},
"headers":{
}
}
}
},
"post":{
"tags":[
"Accounts"
],
"operationId":"create",
"consumes":[
"application/json"
],
"produces":[
"application/json"
],
"parameters":[
{
"in":"body",
"name":"body",
"required":false,
"schema":{
"$ref":"#/definitions/AccountCreatingRequest"
}
}
],
"responses":{
"200":{
"description":"successful operation",
"schema":{
"$ref":"#/definitions/BaseResponse"
},
"headers":{
}
}
}
}
},
"/accounts/transactions/{number}":{
"get":{
"tags":[
"Accounts"
],
"operationId":"loadTransactions",
"produces":[
"application/json"
],
"parameters":[
{
"name":"number",
"in":"path",
"required":true,
"type":"string"
}
],
"responses":{
"200":{
"description":"successful operation",
"schema":{
"$ref":"#/definitions/BaseResponse"
},
"headers":{
}
}
}
}
},
"/accounts/{number}":{
"get":{
"tags":[
"Accounts"
],
"operationId":"load",
"produces":[
"application/json"
],
"parameters":[
{
"name":"number",
"in":"path",
"required":true,
"type":"string"
}
],
"responses":{
"200":{
"description":"successful operation",
"schema":{
"$ref":"#/definitions/BaseResponse"
},
"headers":{
}
}
}
}
},
"/process":{
"put":{
"tags":[
"Process"
],
"operationId":"create",
"consumes":[
"application/json"
],
"produces":[
"application/json"
],
"parameters":[
{
"in":"body",
"name":"body",
"required":false,
"schema":{
"$ref":"#/definitions/ProcessRequest"
}
}
],
"responses":{
"200":{
"description":"successful operation",
"schema":{
"$ref":"#/definitions/BaseResponse"
},
"headers":{
}
}
}
}
},
"/users":{
"get":{
"tags":[
"Users"
],
"operationId":"loadAll",
"produces":[
"application/json"
],
"parameters":[
],
"responses":{
"200":{
"description":"successful operation",
"schema":{
"type":"array",
"items":{
"$ref":"#/definitions/UserDto"
}
},
"headers":{
}
}
}
}
},
"/users/transactions/{login}":{
"get":{
"tags":[
"Users"
],
"operationId":"loadTransactions",
"produces":[
"application/json"
],
"parameters":[
{
"name":"login",
"in":"path",
"required":true,
"type":"string"
}
],
"responses":{
"200":{
"description":"successful operation",
"schema":{
"$ref":"#/definitions/BaseResponse"
},
"headers":{
}
}
}
}
},
"/users/{login}":{
"get":{
"tags":[
"Users"
],
"operationId":"load",
"produces":[
"application/json"
],
"parameters":[
{
"name":"login",
"in":"path",
"required":true,
"type":"string"
}
],
"responses":{
"200":{
"description":"successful operation",
"schema":{
"$ref":"#/definitions/BaseResponse"
},
"headers":{
}
}
}
},
"post":{
"tags":[
"Users"
],
"operationId":"create",
"produces":[
"application/json"
],
"parameters":[
{
"name":"login",
"in":"path",
"required":true,
"type":"string"
}
],
"responses":{
"200":{
"description":"successful operation",
"schema":{
"$ref":"#/definitions/BaseResponse"
},
"headers":{
}
}
}
}
}
},
"definitions":{
"BaseResponse":{
"type":"object",
"properties":{
"code":{
"type":"integer",
"format":"int32"
},
"errorMessage":{
"type":"string"
}
}
},
"ProcessRequest":{
"type":"object",
"properties":{
"fromAccount":{
"type":"string"
},
"toAccount":{
"type":"string"
},
"amount":{
"type":"number"
}
}
},
"UserDto":{
"type":"object",
"properties":{
"login":{
"type":"string"
},
"accounts":{
"type":"array",
"items":{
"$ref":"#/definitions/AccountDto"
}
}
}
},
"AccountCreatingRequest":{
"type":"object",
"properties":{
"userLogin":{
"type":"string"
},
"number":{
"type":"string"
},
"amount":{
"type":"number"
}
}
},
"AccountDto":{
"type":"object",
"properties":{
"accountHolder":{
"type":"string"
},
"amount":{
"type":"number"
},
"number":{
"type":"string"
}
}
}
}
}
```