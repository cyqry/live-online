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
public class AnchorProperty {
    private List<Gift> gifts;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Gift {
        Integer giftId;
        Integer count;


        public Gift Clone() {
            Gift clone = new Gift();
            if (this.giftId != null) {
                clone.giftId = this.giftId;
            }
            if (this.count != null) {
                clone.count = this.count;
            }
            return clone;
        }
    }

    @Test
    public void test() {
        AnchorProperty property = new AnchorProperty();
        AnchorProperty property1 = new AnchorProperty();

        ArrayList<Gift> list = new ArrayList<>();
        list.add(new Gift(2, 6));
        list.add(null);
        list.add(new Gift());
        list.add(new Gift(1, 7));
        property1.gifts = list;

        property.add(property1);
        System.out.println(property);
        property1.add(property);
        System.out.println(property1);
        property.getGifts().add(new Gift(9, 6));
        property1.add(property);
        System.out.println(property1);
        AnchorProperty midel = new AnchorProperty();
        midel.gifts = clone(property1.gifts);
        midel.add(property);

        AnchorProperty test = new AnchorProperty();
        test.gifts = new ArrayList<>();
        test.gifts.add(new Gift(12, -12));
        test.gifts.add(new Gift(65, 234));
        String s = test.toString();
        test.add(midel);
        test.subtract(property1);
        test.add(midel);
        test.subtract(property);
        test.subtract(midel);
        test.add(property);
        test.add(midel);
        test.subtract(property);
        test.subtract(property);
        test.subtract(property1);
        String e = test.toString();
        System.out.println(s.equals(e));
        System.out.println(s);
        System.out.println(e);
    }


    public void add(AnchorProperty property) {
        if (property == null)
            return;
        if (property.getGifts() != null) {
            if (this.gifts == null)
                this.gifts = clone(property.getGifts());
            else {
                for (Gift gift : property.getGifts()) {
                    if (gift != null && gift.getCount() != null && gift.getGiftId() != null) {
                        if (this.gifts.stream().noneMatch(c -> c != null && gift.getGiftId().equals(c.getGiftId()))) {
                            this.gifts.add(gift.Clone());
                        } else {
                            for (Gift c : this.gifts) {
                                if (c != null && c.getGiftId() != null) {
                                    if (Objects.equals(c.getGiftId(), gift.getGiftId())) {
                                        c.setCount(mergeInteger(c.getCount(), 1, gift.getCount()));
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

    private List<Gift> clone(List<Gift> gifts) {
        if (gifts == null)
            return null;
        ArrayList<Gift> list = new ArrayList<>();
        gifts.forEach(c -> {
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

    public boolean subtract(AnchorProperty property) {
        if (property == null)
            return true;
        if (property.getGifts() != null) {
            if (this.gifts != null) {
                for (Gift gift : property.getGifts()) {
                    if (gift != null && gift.getCount() != null && gift.getGiftId() != null) {
                        boolean cut = false;
                        for (Gift g : this.gifts) {
                            if (g != null && g.getGiftId() != null) {
                                if (Objects.equals(g.getGiftId(), gift.getGiftId())) {
                                    Integer result = mergeInteger(g.getCount(), -1, gift.getCount());
                                    if (result < 0)
                                        return false;
                                    g.setCount(result);
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
