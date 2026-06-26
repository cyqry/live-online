package com.ytyo.CommonConst;


import com.ytyo.Model.Property;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GiftConst {
    public static final Map<Integer, Property> GIFT_PRICES;

    static {
        GIFT_PRICES = new ConcurrentHashMap<>();

        //80炫币
        List<Property.Currency> price1 = List.of(new Property.Currency(1, 80));
        //1炫币 50炫点
        List<Property.Currency> price2 = List.of(new Property.Currency(1, 1), new Property.Currency(2, 50));
        //50炫点
        List<Property.Currency> price3 = List.of(new Property.Currency(2, 50));

        GIFT_PRICES.put(1, new Property(price1));
        GIFT_PRICES.put(2, new Property(price2));
        GIFT_PRICES.put(3, new Property(price3));
    }
}
