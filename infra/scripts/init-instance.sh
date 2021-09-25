#!/bin/sh
EC2_IP="$1"
if [ "$EC2_IP" = "" ]; then
    echo "Please provide EC2_IP as param"
    exit
fi
EC2_USER="ec2-user"
RUN_CMD="sudo yum update -y; sudo yum install docker -y; sudo service docker start; sudo usermod -a -G docker ec2-user"
ssh $EC2_USER@$EC2_IP "$RUN_CMD"