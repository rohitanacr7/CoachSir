package com.rohitanabhavane.coachsir.Model;

public class BannerModel {
    String BannerImgLink,BannerID;

    public BannerModel() {
    }

    public BannerModel(String bannerImgLink, String bannerID) {
        BannerImgLink = bannerImgLink;
        BannerID = bannerID;
    }

    public String getBannerImgLink() {
        return BannerImgLink;
    }

    public void setBannerImgLink(String bannerImgLink) {
        BannerImgLink = bannerImgLink;
    }

    public String getBannerID() {
        return BannerID;
    }

    public void setBannerID(String bannerID) {
        BannerID = bannerID;
    }
}

