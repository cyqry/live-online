package com.ytyo.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Property {
    private List<Currency> currencies;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Currency {
        Integer currencyId;
        Integer count;


        public Currency Clone() {
            Currency clone = new Currency();
            if (this.currencyId != null) {
                clone.currencyId = this.currencyId;
            }
            if (this.count != null) {
                clone.count = this.count;
            }
            return clone;
        }
    }

    @Test
    public void test() {
        Property property = new Property();
        Property property1 = new Property();

        ArrayList<Currency> list = new ArrayList<>();
        list.add(new Currency(2, 6));
        list.add(null);
        list.add(new Currency());
        list.add(new Currency(1, 7));
        property1.currencies = list;
        property.currencies = clone(list);
        property.currencies.add(new Currency(3, 7));
        Currency currency = property.currencies.get(3);
        currency.setCount(currency.getCount() - 1);

        System.out.println(property1.subtract(property));
        System.out.println(property1);
    }


    public void add(Property property) {
        if (property == null)
            return;
        if (property.getCurrencies() != null) {
            if (this.currencies == null)
                this.currencies = clone(property.getCurrencies());
            else {
                for (Currency currency : property.getCurrencies()) {
                    if (currency != null && currency.getCount() != null && currency.getCurrencyId() != null) {
                        if (this.currencies.stream().noneMatch(c -> c != null && currency.getCurrencyId().equals(c.getCurrencyId()))) {
                            this.currencies.add(currency.Clone());
                        } else {
                            for (Currency c : this.currencies) {
                                if (c != null && c.getCurrencyId() != null) {
                                    if (Objects.equals(c.getCurrencyId(), currency.getCurrencyId())) {
                                        c.setCount(mergeInteger(c.getCount(), 1, currency.getCount()));
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static List<Currency> clone(List<Currency> currencies) {
        if (currencies == null)
            return null;
        ArrayList<Currency> list = new ArrayList<>();
        currencies.forEach(c -> {
            list.add(c == null ? null : c.Clone());
        });
        return list;
    }

    private Integer mergeInteger(Integer value1, int option, Integer value2) {
        if (value1 == null)
            return value2 == null ? null : option * value2;
        if (value2 == null)
            return value1;
        return value1 + option * value2;
    }

    //当不够减,返回false
    public boolean subtract(Property property) {
        if (property == null)
            return true;
        if (property.getCurrencies() != null) {
            if (this.currencies != null) {
                for (Currency currency : property.getCurrencies()) {
                    if (currency != null && currency.getCount() != null && currency.getCurrencyId() != null) {

                        boolean cut = false;
                        for (Currency c : this.currencies) {
                            if (c != null && c.getCurrencyId() != null) {
                                if (Objects.equals(c.getCurrencyId(), currency.getCurrencyId())) {
                                    Integer result = mergeInteger(c.getCount(), -1, currency.getCount());
                                    if (result < 0)
                                        return false;
                                    c.setCount(result);
                                    cut = true;
                                    break;
                                }
                            }
                        }
                        if (!cut) {
                            return false;
                        }
                    }
                }
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }

    }
}
