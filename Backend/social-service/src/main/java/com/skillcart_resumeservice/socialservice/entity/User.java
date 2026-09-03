package com.skillcart_resumeservice.socialservice.entity;


import jakarta.persistence.Entity;

@Entity
public class User {

    private String username;
    private Long post;
    private Long followers_Count;
    private Long followings_Count;
    private Long Posts;
}
