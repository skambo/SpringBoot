terraform {
  backend "remote" {
    organization = "Sandra"

    workspaces {
      name = "ExampleWorkspace"
    }
  }

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 3.27"
    }
  }
}

provider "aws" {
  profile = "default"
  region  = var.region
}

resource "aws_instance" "example" {
  ami           = "ami-079d9017cb651564d"
  instance_type = var.ec2_instance_type

  tags = {
    Name        = var.ec2_instance_name
    Owner       = "Engineering"
    Status      = "Active"
    Environment = var.environment
  }
}
