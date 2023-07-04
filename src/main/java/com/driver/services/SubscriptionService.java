package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.*;
import com.driver.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){
        // Save The subscription Object into the Db and return the 
        // total Amount that user has to pay

        Subscription subscription=new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        subscription.setStartSubscriptionDate(new Date());

        int total=0;
        if(subscription.getSubscriptionType().equals(SubscriptionType.BASIC)){
            total = 500 + 200*(subscriptionEntryDto.getNoOfScreensRequired());
        }
        if(subscription.getSubscriptionType().equals(SubscriptionType.PRO)){
            total = 800 + 250*(subscriptionEntryDto.getNoOfScreensRequired());
        }
        if(subscription.getSubscriptionType().equals(SubscriptionType.ELITE)){
            total = 1000 + 300*(subscriptionEntryDto.getNoOfScreensRequired());
        }
        subscription.setTotalAmountPaid(total);

        User user=userRepository.findById(subscriptionEntryDto.getUserId()).get();
        user.setSubscription(subscription);
        subscription.setUser(user);
        subscriptionRepository.save(subscription);
        userRepository.save(user);
        return total;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user=userRepository.findById(userId).get();
        if(user.getSubscription().getSubscriptionType().equals(SubscriptionType.ELITE)){
            throw new Exception("Already the best Subscription");
        }

        int diff=0;
        Subscription subscription=user.getSubscription();

        if(subscription.getSubscriptionType().equals(SubscriptionType.BASIC)){
            int screens = subscription.getNoOfScreensSubscribed();

            int tc = 800 + 250*screens;
            diff= tc - subscription.getTotalAmountPaid();
            subscription.setSubscriptionType(SubscriptionType.PRO);
            subscription.setTotalAmountPaid(tc);

            user.setSubscription(subscription);
            subscriptionRepository.save(subscription);
            userRepository.save(user);
        }
        if(subscription.getSubscriptionType().equals(SubscriptionType.PRO)){
            int screens = subscription.getNoOfScreensSubscribed();

            int tc = 1000 + 350*screens;
            diff = tc - subscription.getTotalAmountPaid();
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            subscription.setTotalAmountPaid(tc);

            user.setSubscription(subscription);
            subscriptionRepository.save(subscription);
            userRepository.save(user);
        }
        return diff;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription> subscriptionList=subscriptionRepository.findAll();
        int total=0;
        for(Subscription subscription:subscriptionList){
            total+=subscription.getTotalAmountPaid();
        }
        return total;
    }

}
