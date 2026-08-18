package com.firefox.center.sys.common.wx.mini;

public class PayConsts {

    public static final String URL_GET_TOKEN = "/yninter/getToken";
    public static final String URL_QUERY_VENDOR = "/yninter/queryVendor";
    public static final String URL_ADD_SUB_VENDOR_RECORD = "/yninter/addSubVendorRecord";
    public static final String URL_PLACE_ORDER = "/yninter/icbcHidEPay";
    public static final String URL_FAST_REFUND_V1 = "/yninter/fastRefundV1";
    public static final String URL_QUERYPAYSTATUS = "/yninter/queryEPayStatus";
    public static final String URL_PAY_SIGN = "/yninter/wallet/nc/pay/agreement/sign";
    public static final String URL_PAY_SIGN_SMS = "/yninter/wallet/agreement/sendSMS";
    public static final String URL_PAY_NOCARD = "/yninter/wallet/nc/pay/order/pay";
    public static final String URL_PAY_QRCODE = "/yninter/qrcode/qrCodeScanned";
    public static final String URL_SYNC_BILL = "/integrated/query/queryBillDetail";


    public static String PRIVATE_KEY = "MIIJQgIBADANBgkqhkiG9w0BAQEFAASCCSwwggkoAgEAAoICAQCmt1N766w1FmS/\n" +
            "Imm+WYLHFwBLO90b0rHWQt7peed2I6nfO6yM2F09RCVepAmAYwz7s8jiG7SatQFK5STnDq01xzjw5mWYSvo1sm6TTTy4JTFddS24ZRQd9BQitfabSwiIq6wCxe1vKnq6PzDjjg7Ip483iYb/gFcpnYt84q9yeYphde7V9BVlum7uC7XtzeRUWo+CbJuEeGy4\n" +
            "Q972a+0KWPlxRvWM/KbDUYOfBDokAQxCC47C+l/k0r57Oriu17voxIyWTWNKPtCQKvv+ZYSBwjXD7xjPF/+M25lfqN0xU6komjSZi31sG8rQtfBS9kibruo9fyuIzOF9\n" +
            "hrcGHNsBolHhi2x1OXt8uR4R9vJ2BIGwSe80Nm86M3Oa9Pb/XNOEx13dyj4YEQzGo6PYzZh1cXaRJeXgCbEDTgXhOztOJV0z9PLxbAo2qJWp5ccTqASTuCcCyP87QRl/\n" +
            "PWa7TYUd2KttO++dRkihscO10J7ovg3hhkAskOpFd8PJmMbbwcICWDprKx70xmGllFs++PM3exd6lGjQnRrXx9ykAFV1M15GgeB0sNuitq5Jf1PauXPFn9VtzNSHrxm1\n" +
            "MAi2EHhinHl98UwVGxidaxPFmPynJoXqBcwSLKFpVsFXlnABCmrHXN8nx4IDFiSBlfHm0Ko606LlEwJJO48gfuoCi/XXyQIDAQABAoICACoMGKkrcl5dTIGMW2Ef0EUI\n" +
            "PxG6gnSu+h1Q9kmqbj80lXZw5X8MXs/B7S7mbmMMGE0vNd4ZIL7YIgBStLD12aeznF/drAfx+RMr8omCNGs5Li8lC6fzDlTTp3RtsknObdOSgKqeYtjjvNpWb60kugPP\n" +
            "huZ58CtmohUj3/dacOeNUeJszRT3H6Jtl+a/212jzQ+dW46Id9y8RqdertA1vi4a7KauP+YUHrcqSukDNuq3vPC3W4U/gooCwPnxgHzQelsln1OVJxKlrxJ9VNr/naDg\n" +
            "a5EEL5yjr9dF13Px8f+r2wEbi2KidLqcc9WZ3Yf/B6cEQZ3LAh7ZlZGENYr8IhvqB2BCWT+08gROPuQeoZhwL5jWW786pEvk5g2nlL0dHGAhFlN+qEARL+3HuUk+Px2b\n" +
            "AfF41lIZOwuMnU7iAKtxs2MeXUmwi+xIyBGQjyjY4VubUWsYoZsvjMlXFiaoZbBAAbRRithTww9874n9e6iS1GJFyhecAJFwPOux1i3u6DvsH5kO9ifTau2lJwSilnRh\n" +
            "oCTeTqH6R2Xgpi696ahWUZmRAp/tefET6KaUlfHwOfLNl9R0tU8n+GjnA/GScIijyUrZ0WkeeQ5oxyBfjjyrcwc8leaa4fgIc7EI94yFFQP+BQSIyyqLmsaKFV0jag1W\n" +
            "K/2dpUqs6OAGeb+AFqOBAoIBAQDaTnxStlIzopc9gwK/YNQi/DSHTO9pYtlFoXwIMj5JGNvj5S9q8pL/sN5MdRaZj9LyOip7278p9C0oGQSPP/TDwVfTX0riepSG3Ot2\n" +
            "t3EUquc4bS3rAuT+ufxJvl/3/q/ynRxu+GrEUoazfC96nmWo3ZNT76kd0NCz8HE9rKc4drt3IB7R8bT+KJvLE3NzI2OY35WfR3kAkEEqB6C0Vz94etzTMYCxxs+d20EI\n" +
            "jBFc6TBdQLcZ8VzLhG4NFkE0GS+Uab/NwXHDh4XcX128HEihVE+BBdKi+Epwa1fiNnDqh4//IfS2gI9AvQ3HM4/Ti5DzyT3feqPf+xp/iEKhTQkRAoIBAQDDgHS6raYIQI2hmDQ5ohhOF10oJfAjTtgy0gD6FQRzr02oUOI16kp0Z4YnB5cTnKe64w8pKu7O\n" +
            "9kToWPHO2iLXsQDzJzQFq/6Ngx6KzeHrrL5Ylmd5oOYnu/nmq9CF05xx0diAh7eZ/hvcVFWcTqHdfqhFi3w/CrB1W6+6H22fnAyPzGwPGDw980uL+A0K8dby8+EUHSP9\n" +
            "2KEttso09M4J6KJP8Nb4cGVp7GEVDxW61WefzvaIHQRdvj7UEceLgfvnrWJu3uyh+8pKbPUiFSITZKQkg8laSiZnpiA37Nf+iKRG2QXQSUe+NrWqGb8CVhVPi0mZDf+u\n" +
            "IsbVFnqMeaM5AoIBABwxprzH7zXb+cxn7yLoTn12NRKETSc/LPBhOrOUSZcHLt7v6n/VdEaxgQQ+2vxaJtRBztrQNpAkiPB0yrH1gJcHkWArHnDBhA0m2wIqDuCscdBH\n" +
            "yXZgmVKMkpizjZFpwy+COvnZ+2//eIYVs8wSSO7WLwDYIu9G2K1kkDt24OHZ87we2dwja0yn0fcYPADSwTotJhW8FVSNTN0wvwCLhBb7Rym/au+KzaWFe5CvBk8JjsV8\n" +
            "Ziljkqh2IgBur10bI5n3Tl40bYzjI9aiOmjg/kBXsyzmo52Ik5SRSHher5aai25X5FLtp5Kk1/KyJhEcJUxW4TbLj/6NX464+mH6oQECggEAbKuhtwbBTML9Nz7QgNy+X+IS0TdNZYL1IEKlcl1ubRObuhzDsS57lnrZEibg7nDct0Bxc/O+bOJtaS3Ps6iD\n" +
            "R5vHKGG5RBHX3DdvKb7JfuQ1pi5sdCpg5ONTRMpqjYkT5By+GKvX/cxHFlD2iX0X8nPt+0JOnQhHD+UYKABCQj9OqDv8MOPK0LDFZJdMQLIvRjsT+Mc982OQLEX33jnZ\n" +
            "sZ5TYtY/2Dc52Agh6r/I12K6SBWV6hEfFTCj4ePEJKLrMFurZG+b32qDhp1MIVbX6ym9J+LtcLfY4zJvpEaNxZmUnyERXzbsnynwDw5io1zQWbfWADLM1bHVDiuBkUYn\n" +
            "2QKCAQEAo2O6bpoaXZJPLeAFz9chxQ3/heOz2fwmxavxAdwEY3J18XVx3eQHXZCsws5Z588RN6Rt0QouDIxB7msUTj0l1oSdMDZVi/rklrj2kcVjr5BnPeHnOcz981fNnDAEiOD/i+qgaT6KVo0lu2B4bYwo334vNjIXFmkT5WjSKNu8Ew9eFXYGb2UneWEv\n" +
            "2s5BLma/FJ9mPZoePDMKDtFx0Tg/E4RN+QJ0Mji1r+iiPmSWtoXEZXy2kTxO2QX5uSQuqcQ+T3m6pjv+fTYQeWbovSuYQF+DFDO+RUTJU8ZZiuu90/194SWyXiJFrDL8Z1SJ++oZbEWedfNYh10SOnt6uC1Rag==";//私钥

    public static String PUBLIC_KEY = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAprdTe+usNRZkvyJpvlmCxxcASzvdG9Kx1kLe6XnndiOp3zusjNhdPUQlXqQJgGMM+7PI4hu0mrUBSuUk5w6tNcc48OZlmEr6NbJuk008uCUxXXUtuGUUHfQUIrX2m0sIiKusAsXtbyp6uj8w444OyKePN4mG/4BXKZ2LfOKvcnmKYXXu1fQVZbpu7gu17c3kVFqPgmybhHhsuEPe9mvtClj5cUb1jPymw1GDnwQ6JAEMQguOwvpf5NK+ezq4rte76MSMlk1jSj7QkCr7/mWEgcI1w+8Yzxf/jNuZX6jdMVOpKJo0mYt9bBvK0LXwUvZIm67qPX8riMzhfYa3BhzbAaJR4YtsdTl7fLkeEfbydgSBsEnvNDZvOjNzmvT2/1zThMdd3co+GBEMxqOj2M2YdXF2kSXl4AmxA04F4Ts7TiVdM/Ty8WwKNqiVqeXHE6gEk7gnAsj/O0EZfz1mu02FHdirbTvvnUZIobHDtdCe6L4N4YZALJDqRXfDyZjG28HCAlg6ayse9MZhpZRbPvjzN3sXepRo0J0a18fcpABVdTNeRoHgdLDborauSX9T2rlzxZ/VbczUh68ZtTAIthB4Ypx5ffFMFRsYnWsTxZj8pyaF6gXMEiyhaVbBV5ZwAQpqx1zfJ8eCAxYkgZXx5tCqOtOi5RMCSTuPIH7qAov118kCAwEAAQ==";//公钥


}
