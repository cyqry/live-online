package com.ytyo.Model;


import java.util.List;


public record SendGiftEntity(Long roomId, List<AnchorProperty.Gift> gifts) {
}
