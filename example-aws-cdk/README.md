## Necessary files

.aws/credentials:

```
[default]
aws_access_key_id = <access-key-id>
aws_secret_access_key = <secret-access-key>
```

## Deployment steps

1. Configure AWS CLI:

   ```bash
   aws configure
   ```

2. Create IAM user:

    ```bash
    aws iam create-user --user-name <user-name>
    ```

3. Create access key:

   ```bash
   aws iam create-access-key --user-name <user-name>
   ```

4. Export certificate arn, which maps to SSL/TLS certificate issued by AWS:

    ```bash
    aws acm list-certificates
   
    export CERTIFICATE_ARN=<certificate-arn>
    ```

5. Export default VPC to use other AWS services (e.g. RDS):

    ```bash
    aws ec2 describe-vpcs
   
    export VPC_ID=<vpc-id>
    ```

6. Deploy CloudFormation stack:

   ```bash
   aws bootstrap

   aws deploy
   ```