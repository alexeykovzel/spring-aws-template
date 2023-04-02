package com.alexeykovzel.example;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.elasticbeanstalk.*;
import software.amazon.awscdk.services.iam.*;
import software.amazon.awscdk.services.s3.assets.Asset;
import software.amazon.awscdk.services.s3.assets.AssetProps;
import software.amazon.awscdk.services.ec2.*;
import software.constructs.Construct;

import java.util.List;
import java.util.stream.Collectors;

public class ElasticBeanstalkStack extends Stack {

    // Stack configuration
    private static final String ASSET_PATH = "../example-web/build/libs/example-web.jar";
    private static final String STACK_NAME = "64bit Amazon Linux 2 v3.4.5 running Corretto 11";
    private static final String INSTANCE_TYPE = "t2.micro";
    private static final int SERVER_PORT = 5000;

    // IDs of resources
    private static final String PREFIX = "example";
    private static final String APP_ID = "example-app";
    private static final String IAM_PROFILE_ID = PREFIX + "-iam-profile";
    private static final String IAM_ROLE_ID = PREFIX + "-iam-role";
    private static final String VERSION_ID = PREFIX + "-version";
    private static final String EB_APP_ID = PREFIX + "-eb-app";
    private static final String EB_ENV_ID = PREFIX + "-eb-env";
    private static final String STACK_ID = PREFIX + "-eb-stack";
    private static final String ASSET_ID = PREFIX + "-asset";

    public ElasticBeanstalkStack(Construct scope, StackProps props) {
        super(scope, STACK_ID, props);

        // Create ElasticBeanStalk app
        var app = new CfnApplication(this, EB_APP_ID, CfnApplicationProps.builder()
                .applicationName(APP_ID)
                .build());

        // Create S3 asset from the JAR file
        var asset = new Asset(this, ASSET_ID, AssetProps.builder()
                .path(ASSET_PATH)
                .build());

        var sourceBundle = CfnApplicationVersion.SourceBundleProperty.builder()
                .s3Bucket(asset.getS3BucketName())
                .s3Key(asset.getS3ObjectKey())
                .build();

        // Create app version from the S3 asset defined earlier
        var version = new CfnApplicationVersion(this, VERSION_ID, CfnApplicationVersionProps.builder()
                .applicationName(APP_ID)
                .sourceBundle(sourceBundle)
                .build());

        // Make sure that the app exists before creating an app version
        version.addDependsOn(app);

        // Create IAM role and instance profile
        var role = new Role(this, IAM_ROLE_ID, RoleProps.builder()
                .assumedBy(new ServicePrincipal("ec2.amazonaws.com"))
                .build());

        var policy = ManagedPolicy.fromAwsManagedPolicyName("AWSElasticBeanstalkWebTier");
        role.addManagedPolicy(policy);

        var profile = new CfnInstanceProfile(this, IAM_PROFILE_ID, CfnInstanceProfileProps.builder()
                .instanceProfileName(IAM_PROFILE_ID)
                .roles(List.of(role.getRoleName()))
                .build());

        // Get default VPC
        var vpc = Vpc.fromLookup(this, "default-vpc", VpcLookupOptions.builder()
                .isDefault(true)
                .build());

        // Get public subnets
        var publicSubnets = vpc.getPublicSubnets().stream()
                .map(ISubnet::getSubnetId)
                .collect(Collectors.joining(","));

        // Set option settings of the environment
        List<CfnEnvironment.OptionSettingProperty> settings = new EnvSettingBuilder()
                .add("aws:elasticbeanstalk:application:environment", "SERVER_PORT", String.valueOf(SERVER_PORT))
                .add("aws:autoscaling:launchconfiguration", "IamInstanceProfile", profile.getRef())
                .add("aws:ec2:instances", "InstanceTypes", INSTANCE_TYPE)

                /* VPC */
                .add("aws:ec2:vpc", "VPCId", vpc.getVpcId())
                .add("aws:ec2:vpc", "Subnets", publicSubnets)

                /* Load balancer */
                .add("aws:elasticbeanstalk:environment", "EnvironmentType", "LoadBalanced")
                .add("aws:elasticbeanstalk:environment", "LoadBalancerType", "application")
                .add("aws:elbv2:listener:443", "SSLCertificateArns", System.getenv("CERTIFICATE_ARN"))
                .add("aws:elbv2:listener:443", "ListenerEnabled", "true")
                .add("aws:elbv2:listener:443", "Protocol", "HTTPS")

                /* Autoscaling */
                .add("aws:autoscaling:asg", "MinSize", "1")
                .add("aws:autoscaling:asg", "MaxSize", "1")
                .build();

        // Create Elastic Beanstalk environment to run the application
        new CfnEnvironment(this, EB_ENV_ID, CfnEnvironmentProps.builder()
                .applicationName(app.getApplicationName())
                .environmentName(EB_ENV_ID)
                .solutionStackName(STACK_NAME)
                .optionSettings(settings)
                .versionLabel(version.getRef())
                .build());
    }
}
