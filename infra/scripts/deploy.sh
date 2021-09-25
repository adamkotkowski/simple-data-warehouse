#!/bin/sh
EC2_IP="$1"
AWS_ACCOUNT_ID="$2"
AWS_REGION="$3"
if [ "$EC2_IP" = "" ] || [ "$AWS_ACCOUNT_ID" = "" ] || [ "$AWS_REGION" = "" ]; then
    echo "Please provide EC2_IP, AWS_ACCOUNT_ID, AWS_REGION as params"
    echo "    example usage: deploy.sh 90.111.111.111 774736374211 eu-central-1"
    exit
fi
IMAGE_NAME="$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/simple_data_warehouse"
EC2_USER="ec2-user"

DOCKER_LOGIN_CMD="aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com"
REMOVE_CONTAINER_CMD="docker rm \$(docker stop \$(docker ps -a -q --filter ancestor=$IMAGE_NAME --format="{{.ID}}"))"
RUN_CMD="docker run --restart=always -t -p 80:8080 --name service $IMAGE_NAME"
ssh $EC2_USER@$EC2_IP "$DOCKER_LOGIN_CMD; $REMOVE_CONTAINER_CMD; $RUN_CMD"