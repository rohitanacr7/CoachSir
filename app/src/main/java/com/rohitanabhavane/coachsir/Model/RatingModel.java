package com.rohitanabhavane.coachsir.Model;

public class RatingModel {
    String FullName, Comment, Mobile, Email, ProfileImgLink, rating, RatingDateTime;

    public RatingModel() {

    }

    public RatingModel(String fullName, String comment, String mobile, String email, String profileImgLink, String rating, String ratingDateTime) {
        FullName = fullName;
        Comment = comment;
        Mobile = mobile;
        Email = email;
        ProfileImgLink = profileImgLink;
        this.rating = rating;
        RatingDateTime = ratingDateTime;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getProfileImgLink() {
        return ProfileImgLink;
    }

    public void setProfileImgLink(String profileImgLink) {
        ProfileImgLink = profileImgLink;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRatingDateTime() {
        return RatingDateTime;
    }

    public void setRatingDateTime(String ratingDateTime) {
        RatingDateTime = ratingDateTime;
    }
}
