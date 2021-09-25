#!/bin/sh
AWS_ACCOUNT_ID="$1"
AWS_REGION="$2"
if [ "$AWS_ACCOUNT_ID" = "" ] || [ "$AWS_REGION" = "" ]; then
    echo "Please provide AWS_ACCOUNT_ID and AWS_REGION as param"
    echo "    example usage: publish-image.sh 774736374211 eu-central-1"
    exit
fi

#build
docker build -t $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/simple_data_warehouse .

#login
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com

#push
docker push $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/simple_data_warehouse
