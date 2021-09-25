resource "aws_s3_bucket" "data-bucket" {
  bucket = "${var.s3_bucket}"
  acl = "public-read"
  policy = <<EOF
{
  "Version":"2012-10-17",
  "Statement":[
    {
      "Sid":"PublicRead",
      "Effect":"Allow",
      "Principal": "*",
      "Action":["s3:GetObject","s3:GetObjectVersion"],
      "Resource":["arn:aws:s3:::${var.s3_bucket}/*"]
    }
  ]
}
EOF
  tags = {
    Name = "Simple Data Warehouse bucket"
  }
}

resource "aws_s3_bucket_object" "data-file" {
  bucket = "${var.s3_bucket}"
  key = "data/data.csv"
  source = "data/data.csv"
}

resource "aws_athena_database" "simple-data-warehouse-athena-db" {
  name = "${var.athena_db_name}"
  bucket = "${var.s3_bucket}"
}
resource "aws_glue_catalog_table" "aws_glue_catalog_table" {
  name = "${var.athena_table_name}"
  database_name = "${var.athena_db_name}"
  table_type = "EXTERNAL_TABLE"

  parameters = {
    EXTERNAL = "TRUE"
  }

  storage_descriptor {
    location = "s3://${var.s3_bucket}/data/"
    input_format = "org.apache.hadoop.mapred.TextInputFormat"
    output_format = "org.apache.hadoop.mapred.TextInputFormat"

    ser_de_info {
      name = "my-serde"
      serialization_library = "org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe"

      parameters = {
        "field.delim" = ","
        "skip.header.line.count" = "1"
      }
    }

    columns {
      name = "datasource"
      type = "string"
    }
    columns {
      name = "campaign"
      type = "string"
    }
    columns {
      name = "daily"
      type = "string"
    }
    columns {
      name = "clicks"
      type = "bigint"
    }
    columns {
      name = "impressions"
      type = "bigint"
    }
  }
}