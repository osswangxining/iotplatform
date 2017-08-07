package org.iotp.infomgt.data.security;

public class AssetTokenCredentials implements AssetCredentialsFilter {

    private final String token;

    public AssetTokenCredentials(String token) {
        this.token = token;
    }

    @Override
    public AssetCredentialsType getCredentialsType() {
        return AssetCredentialsType.ACCESS_TOKEN;
    }

    @Override
    public String getCredentialsId() {
        return token;
    }

    @Override
    public String toString() {
        return "AssetTokenCredentials [token=" + token + "]";
    }

}
