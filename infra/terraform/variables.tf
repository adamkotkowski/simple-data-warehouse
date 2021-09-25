variable "region" {
  default = "eu-central-1"
}
variable "s3_bucket" {
  default = "simple-data-warehouse"
}
variable "athena_table_name" {
  default = "campaigns"
}
variable "athena_db_name" {
  default = "simple_data_warehouse"
}
variable "ecr_repository_name" {
  default = "simple_data_warehouse"
}
variable "project_tag" {
  default = "simple-data-warehouse"
}
variable "ec2_ssh_access_ip" {
  default = "<your_ip_here>"
}
variable "ec2_instance_type" {
  default = "t3.micro"
}
variable "ec2_rsa_public_key" {
  default = "ssh-rsa AAAXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXp+bbbb== email@email.com"
}