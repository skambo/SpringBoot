terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 3.27"
    }
  }
}

provider "aws" {
  profile = "default"
  region  = "eu-west-1"
}

resource "aws_instance" "example" {
  ami           = "ami-079d9017cb651564d"
  instance_type = "t2.micro"

  tags = {
    Name = "ExampleInstance"
  }
}
