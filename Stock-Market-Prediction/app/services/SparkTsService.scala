package services

import Util.Timeseries.spark
import org.apache.spark.sql.{DataFrame}

object SparkTsService {



    //Get the data of all companies in a dataframe
    def getData(): DataFrame = {
        //val spark = SparkSession.builder().appName("Stock-prediction").master("local[*]").getOrCreate();
        //Create dataframe for all company csv and convert to the required format with different date and close price columns
        import spark.implicits._
        val appleDf: DataFrame = createDataFrame("AAPL_data_train")
        val apple = appleDf.select(appleDf("date").as("appleDate"), appleDf("close").as("closeApple"))

        val amazonDf: DataFrame = createDataFrame("AMZN_data_train")
        val amazon = amazonDf.select(amazonDf("date").as("amazonDate"), amazonDf("close").as("closeAmazon"))

        val ebayDf: DataFrame = createDataFrame("EBAY_data_train")
        val ebay = ebayDf.select(ebayDf("date").as("ebayDate"), ebayDf("close").as("closeebay"))

        val expediaDf: DataFrame = createDataFrame("EXPE_data_train")
        val expedia = expediaDf.select(expediaDf("date").as("expediaDate"), expediaDf("close").as("closeexpedia"))

        val facebookDf: DataFrame = createDataFrame("FB_data_train")
        val facebook = facebookDf.select(facebookDf("date").as("facebookDate"), facebookDf("close").as("closefacebook"))

        val googleDf: DataFrame = createDataFrame("GOOGL_data_train")
        val google = googleDf.select(googleDf("date").as("googleDate"), googleDf("close").as("closegoogle"))

        val microsoftDf: DataFrame = createDataFrame("MSFT_data_train")
        val microsoft = microsoftDf.select(microsoftDf("date").as("microsoftDate"), microsoftDf("close").as("closemicrosoft"))

        val tripAdvDf: DataFrame = createDataFrame("TRIP_data_train")
        val tripAdv = tripAdvDf.select(tripAdvDf("date").as("tripAdvDate"), tripAdvDf("close").as("closetripAdv"))

        val walmartDf: DataFrame = createDataFrame("WMT_data_train")
        val walmart = walmartDf.select(walmartDf("date").as("walmartDate"), walmartDf("close").as("closewalmart"))

        //Join all the different dataframes and convert into a single frame
        val data = apple
            .join(amazon, $"appleDate" === $"amazonDate").select($"appleDate", $"closeApple", $"closeAmazon")
            .join(ebay, $"appleDate" === $"ebayDate").select($"appleDate", $"closeApple", $"closeAmazon", $"closeebay")
            .join(expedia, $"appleDate" === $"expediaDate").select($"appleDate", $"closeApple", $"closeAmazon", $"closeebay", $"closeexpedia")
            .join(facebook, $"appleDate" === $"facebookDate").select($"appleDate", $"closeApple", $"closeAmazon", $"closeebay", $"closeexpedia", $"closefacebook")
            .join(google, $"appleDate" === $"googleDate").select($"appleDate", $"closeApple", $"closeAmazon", $"closeebay", $"closeexpedia", $"closefacebook", $"closegoogle")
            .join(microsoft, $"appleDate" === $"microsoftDate").select($"appleDate", $"closeApple", $"closeAmazon", $"closeebay", $"closeexpedia", $"closefacebook", $"closegoogle", $"closemicrosoft")
            .join(tripAdv, $"appleDate" === $"tripAdvDate").select($"appleDate", $"closeApple", $"closeAmazon", $"closeebay", $"closeexpedia", $"closefacebook", $"closegoogle", $"closemicrosoft", $"closetripAdv")
            .join(walmart, $"appleDate" === $"walmartDate").select($"appleDate".as("date"), $"closeApple", $"closeAmazon", $"closeebay", $"closeexpedia", $"closefacebook", $"closegoogle", $"closemicrosoft", $"closetripAdv", $"closewalmart")

        data
    }


    //To create dataframe based on CSV
    def createDataFrame(name: String):DataFrame = {
        return spark.read.option("header", "true").csv(s"../Stock-Market-Prediction/app/$name.csv")
    }

}
