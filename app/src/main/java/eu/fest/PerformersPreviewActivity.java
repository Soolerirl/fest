package eu.fest;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import eu.fest.model.databases.Performers;

@EActivity(R.layout.activity_performers_preview)
public class PerformersPreviewActivity extends FragmentActivity {

    @ViewById
    TextView performers_name;

    @ViewById
    TextView performers_category;

    @ViewById
    TextView performers_genre;

    @ViewById
    TextView performers_start;

    @Extra("performersData")
    Performers performersData;

    String genres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @AfterViews
    void setupUi(){
        performers_name.setText(performersData.getPerformer_name());
        performers_category.setText(performersData.getPerformer_category_name());
        performers_genre.setText(genres);
        performers_start.setText(performersData.getStart_date());
    }
}
