variable "region" {
  type        = string
  description = "This is the AWS region"
  default     = "eu-west-1"
}

variable "ec2_instance_type" {
  type        = string
  description = "This is the AWS instance type"
  default     = "t2.micro"
}

variable "environment" {
  type        = string
  description = "The environment"
}

variable "ec2_instance_name" {
  type        = string
  description = "The name of the EC2 instance"
}
