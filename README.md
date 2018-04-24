
# Stock-Market-Prediction-Using-Scala-and-Spark

This is the CSYE7200 Big Data Systems Engineering Using Scala Final Project for Team 8 Spring 2018

Team Members:

Purva Bundela bundela.p@husky.neu.edu

Dishank Shah  shah.di@husky.neu.edu

# Final Presentation
https://prezi.com/view/Lr7sPPoyMfhfscPKTesr/

 # Abstract
 
To examine a number of different forecasting techniques to predict future stock returns based on past returns and numerical news indicators to construct a portfolio of multiple stocks in order to diversify the risk. We do this by applying supervised learning methods for stock price forecasting by interpreting the seemingly chaotic market data.

# Methodology
## Data cleaning and parsing
1.  Data from all the companies CSV was loaded into dataframes and converted to format required by ARIMA model.

## Spark Timeseries Methodology
1. A time series is a series of data points indexed (or listed or graphed) in time order. Most commonly, a time series is a sequence taken at successive equally spaced points in time.

2. Company name and dates were taken as features to train the data using ARIMA model.

3. Dataframes of all the companies were joined and loaded to RDD.

4. Using ARIMA model data is trained and model is then used for forecasting future values.

5. Using forecast method of ARIMA model stock prices for 30 days.

## Twitter Sentiment Analysis
1. Tweets acquired by Search API are in JSON format with a maximum limit of 100 per request. Built a JSON parser to correctly parse the and filter those attributes which are not required.

2. Special characters are removed to increase the accuracy of the sentiment scores.

3. Using Stanford NLP to calculate the sentiment score which tells whether the particular tweet is positive or negative.

4. Using Spark Streaming to receive the stream of tweets and perform the analysis for past 7 days.

## Web Application and Visualization
1. Web application is created using Play framework.

2. Data is send from controller to view using Akka framework.

2. User should be able to either signUp or login to view the dashboard.

3. To show the analysis, d3.js is used

# Steps to run the project on the local machine
## Run on windows and mac

1. Download sbt 0.13.17 

2. Configure Java 1.8 on your machine

3. Configure scala 2.11.8 on your machine

4. To run from terminal go to the Stock-Market-Prediction and write sbt run.

5. Also make sure to configure your database in config file so that you can signUp and login to the application.


# Dataset
SNAP Twitter7 dataset

Yahoo Finance Stocks dataset

The dataset was taken from Kaggle and had data for around 500 companies.

Each data file had 8 columns 

We trained the model with the data of 10 companies and 15000 rows.
# Details
used HDFS to store dataset

utilized Spark to read and pre-process the dataset

applied Stanford NLP to do sentiment analysis on twitter data

applied Spark-ML to train a Timeseries model

assessed the accuracy with R square and RMSE
# Continuous Integration

This project is using Travis CI as the continuous integration tool  [![Build Status](https://travis-ci.org/PurvaBundela/Stock-Market-Prediction-Using-Scala-and-Spark.svg?branch=master)](https://travis-ci.org/PurvaBundela/Stock-Market-Prediction-Using-Scala-and-Spark) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/3ac16119b50b4677bf6be68eb36a518a)](https://www.codacy.com/app/dishanks9/Stock-Market-Prediction-Using-Scala-and-Spark?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=dishanks9/Stock-Market-Prediction-Using-Scala-and-Spark&amp;utm_campaign=Badge_Grade)

