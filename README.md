
# Stock-Market-Prediction-Using-Scala-and-Spark

This is the CSYE7200 Big Data Systems Engineering Using Scala Final Project for Team 8 Spring 2018

Team Members:

Purva Bundela bundela.p@husky.neu.edu

Dishank Shah  shah.di@husky.neu.edu

# Final Presentation
https://prezi.com/view/Lr7sPPoyMfhfscPKTesr/

## Abstract

## Methodology
# Spark Timeseries Methodology
A time series is a series of data points indexed (or listed or graphed) in time order. Most commonly, a time series is a sequence taken at successive equally spaced points in time.

Preprocessing and cleaning the data Using Scala.

Feature extraction and load into RDD Using Scala.

Training the data using ARIMA model and forecasting the output.

Parse data into desired format and visualize the data.

# Twitter Sentiment Analysis
Tweets acquired by Search API are in JSON format with a maximum limit of 100 per request. Built a JSON parser to correctly parse the and filter those attributes which are not required.

Special characters are removed to increase the accuracy of the sentiment scores.

Using Stanford NLP to calculate the sentiment score which tells whether the particular tweet is positive or negative.

Using Spark Streaming to receive the stream of tweets and perform the analysis for past 7 days.


## Steps to run the project on the local machine


## Continuous Integration

This project is using Travis CI as the continuous integration tool  [![Build Status](https://travis-ci.org/PurvaBundela/Stock-Market-Prediction-Using-Scala-and-Spark.svg?branch=master)](https://travis-ci.org/PurvaBundela/Stock-Market-Prediction-Using-Scala-and-Spark) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/ca8d184a3abc4c1eb0d570df9a75ab33)](https://www.codacy.com/app/dishanks9/Stock-Market-Prediction-Using-Scala-and-Spark_2?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=PurvaBundela/Stock-Market-Prediction-Using-Scala-and-Spark&amp;utm_campaign=Badge_Grade)
## Dataset
SNAP Twitter7 dataset
Yahoo Finance Stocks dataset
## Details
used HDFS to store dataset

utilized Spark to read and pre-process the dataset

applied Stanford NLP to do sentiment analysis on twitter data

applied Spark-ML to train a linear regression model

assessed the accuracy with R square and RMSE



