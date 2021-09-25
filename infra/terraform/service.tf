data "aws_vpc" "default" {
  default = true
}

data "aws_subnet_ids" "all" {
  vpc_id = data.aws_vpc.default.id
}

resource "aws_key_pair" "deployer" {
  key_name   = "deployer-key"
  public_key = var.ec2_rsa_public_key
}

// ECR repo
resource "aws_ecr_repository" "simple-data-warehouse-repo" {
  name = var.ecr_repository_name
  image_tag_mutability = "MUTABLE"

  tags = {
    project = var.project_tag
  }
}


// IAM role
resource "aws_iam_role" "ec2_role_simple_data_warehouse" {
  name = "ec2_role_simple_data_warehouse"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "ec2.amazonaws.com"
      },
      "Effect": "Allow"
    }
  ]
}
EOF

  tags = {
    project = var.project_tag
  }
}

resource "aws_iam_instance_profile" "ec2_profile_simple_data_warehouse" {
  name = "ec2_profile_simple_data_warehouse"
  role = aws_iam_role.ec2_role_simple_data_warehouse.name
}

resource "aws_iam_role_policy" "ec2_policy" {
  name = "ec2_policy"
  role = aws_iam_role.ec2_role_simple_data_warehouse.id

  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "VisualEditor0",
            "Effect": "Allow",
            "Action": [
                "ecr:GetDownloadUrlForLayer",
                "ecr:GetAuthorizationToken",
                "athena:*",
                "glue:GetTable",
                "glue:UpdateTable",
                "ecr:BatchGetImage",
                "s3:*"
            ],
            "Resource": "*"
        }
    ]
}
EOF
}

// security groups

module "dev_ssh_sg" {
  source = "terraform-aws-modules/security-group/aws"

  name        = "ec2_sg"
  description = "Security group for ec2_sg"
  vpc_id      = data.aws_vpc.default.id

  ingress_cidr_blocks = ["${var.ec2_ssh_access_ip}/32"]
  ingress_rules       = ["ssh-tcp"]
}

module "ec2_sg" {
  source = "terraform-aws-modules/security-group/aws"

  name        = "ec2_sg"
  description = "Security group for ec2_sg"
  vpc_id      = data.aws_vpc.default.id

  ingress_cidr_blocks = ["0.0.0.0/0"]
  ingress_rules       = ["http-80-tcp", "https-443-tcp", "all-icmp"]
  egress_rules        = ["all-all"]
}

// ec2
data "aws_ami" "amazon_linux_2" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["amzn2-ami-hvm-*-x86_64-ebs"]
  }
}

resource "aws_instance" "web" {
  ami           = data.aws_ami.amazon_linux_2.id
  instance_type = var.ec2_instance_type

  root_block_device {
    volume_size = 8
  }

  vpc_security_group_ids = [
    module.ec2_sg.security_group_id,
    module.dev_ssh_sg.security_group_id
  ]
  iam_instance_profile = aws_iam_instance_profile.ec2_profile_simple_data_warehouse.name

  tags = {
    project = var.project_tag
  }

  key_name                = "deployer-key"
  monitoring              = true
  disable_api_termination = false
  ebs_optimized           = true
}
