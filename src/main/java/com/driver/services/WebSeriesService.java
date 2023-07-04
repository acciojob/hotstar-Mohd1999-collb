package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.*;
import com.driver.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo

        WebSeries webSeries=new WebSeries();
        if(webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName())!=null){
            throw new Exception("Series is already present");
        }
        webSeries.setSeriesName(webSeriesEntryDto.getSeriesName());
        webSeries.setRating(webSeriesEntryDto.getRating());
        webSeries.setAgeLimit(webSeriesEntryDto.getAgeLimit());
        webSeries.setSubscriptionType(webSeriesEntryDto.getSubscriptionType());

        ProductionHouse productionHouse=productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId()).get();
        webSeries.setProductionHouse(productionHouse);
        productionHouse.getWebSeriesList().add(webSeries);

        double ratings=0;
        for(WebSeries webSeries1: productionHouse.getWebSeriesList()){
            ratings+=webSeries1.getRating();
        }
        ratings /= productionHouse.getWebSeriesList().size();

        productionHouse.setRatings(ratings);
        productionHouseRepository.save(productionHouse);
        WebSeries savedWebSeries=webSeriesRepository.save(webSeries);

        return savedWebSeries.getId();
    }

}
