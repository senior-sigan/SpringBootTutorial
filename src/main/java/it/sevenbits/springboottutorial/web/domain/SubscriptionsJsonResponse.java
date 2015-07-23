package it.sevenbits.springboottutorial.web.domain;

import java.util.List;

public class SubscriptionsJsonResponse {
    private Integer count;
    private List<SubscriptionModel> data;
    
    public SubscriptionsJsonResponse(Integer count, List<SubscriptionModel> data) {
        this.count = count;
        this.data = data;
    }

    public Integer getCount() {
        return count;
    }

    public List<SubscriptionModel> getData() {
        return data;
    }
}
