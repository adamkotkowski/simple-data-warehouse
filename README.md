# Simple Data Warehouse app

Simple POC app to expose data stored on S3 bucket and transformed by Athena.
The exposed API is generic as it takes SQL conditions as params

# Test deployed app location:
Please contact repo owner to get an app location

# How to run
prerequisites:
- aws CLI installed - security credentials configured
- docker running
- `infra/terraform/variables.tf` - `ec2_rsa_public_key` should be set to id_rsa public key, `ec2_ssh_access_ip` should be your IP address to allow ssh

```
./gradlew build                                         # build the spring boot app
terraform -chdir=./infra/terraform apply                # creates infrastructure - S3, Athena, ECR, EC2, etc
./infra/scripts/init-stinstance.sh <IP_OF_CREATED_EC2>    # installs docker on EC2
./infra/scripts/build-publish-image.sh <AWS_ACCOUNT_ID> <AWS_REGION>        # builds docker image and upload to ECR
./infra/scripts/deploy.sh <IP_OF_CREATED_EC2> <AWS_ACCOUNT_ID> <AWS_REGION> # deploy latest image from ECR to EC2
```

# API

**Security:** for POC purpose basic authentication with predefined credentials are used:

```
username: user
password: cf84fef9-d49d-4dd8-8f63-ebddd2f534a7
```
**Rate Limiting:** to avoid potential abuse rate limiting is applied for running query
## Run the data query:
```
POST: /query
request body:
{
    "select":["*"],
    "limit": 20,
    "where": "date > date('2019-11-12') and datasource='Google Ads'",
    "orderBy": "date",
    "groupBy": "",
    "having": ""
}
response:
{
    "id": "<query_execution_id>"
}
```
example curl:
```
curl --location --request POST 'http://<host>>/query' \
--header 'Authorization: Basic dXNlcjpjZjg0ZmVmOS1kNDlkLTRkZDgtOGY2My1lYmRkZDJmNTM0YTc=' \
--header 'Content-Type: application/json' \
--data-raw '{
    "select": [
        "*"
    ],
    "where": "date > date('\''2019-11-12'\'') and datasource='\''Google Ads'\''",
    "limit": 20
}'
```
## Get query result
```
GET: /query/{query_execution_id}
response:
{
    "resultFile": "<url_to_csv_result_file>"
}
```
example curl:
```
curl --location --request GET 'http://<host>/query/ce801748-ccb2-455b-9d84-6aecfccd2f0f' \
--header 'Authorization: Basic dXNlcjpjZjg0ZmVmOS1kNDlkLTRkZDgtOGY2My1lYmRkZDJmNTM0YTc=' \
--header 'Content-Type: application/json' 
```

